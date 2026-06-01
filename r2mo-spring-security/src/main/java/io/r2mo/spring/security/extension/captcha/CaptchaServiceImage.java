package io.r2mo.spring.security.extension.captcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.CaptchaRequest;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.spring.security.config.ConfigSecurityCaptcha;
import io.r2mo.typed.common.Kv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于 Hutool + UserCache 的图形验证码服务实现
 *
 * @author lang : 2025-11-13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaServiceImage implements CaptchaService {

    private final CodeGenerator captchaGenerator;
    private final Font captchaFont;
    private final ConfigSecurityCaptcha configCaptcha;

    @Override
    public Map<String, Object> generate() {
        final String captchaKey = UUID.randomUUID().toString().replace("-", "");

        final ConfigSecurityCaptcha.ConfigStyle style = this.configCaptcha.getStyle();
        final int interfereCount = Math.max(0, this.configCaptcha.getInterfereCount());

        final LineCaptcha captcha = new StyledLineCaptcha(
            this.configCaptcha.getWidth(),
            this.configCaptcha.getHeight(),
            this.captchaGenerator,
            interfereCount,
            style
        );
        captcha.setFont(this.captchaFont);
        if (Objects.nonNull(this.configCaptcha.getTextAlpha())) {
            captcha.setTextAlpha(this.configCaptcha.getTextAlpha());
        }

        final String code = captcha.getCode();

        final Kv<String, String> generated = Kv.create(captchaKey, code);
        UserCache.of().authorize(generated, this.configCaptcha.forArguments());

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            captcha.write(out);
            final String base64Image = Base64.encode(out.toByteArray());
            final Map<String, Object> result = new HashMap<>();
            result.put(CaptchaRequest.ID, captchaKey);
            result.put("image", "data:image/png;base64," + base64Image);
            return result;
        } catch (final Exception e) {
            log.error("验证码生成失败", e);
            throw new RuntimeException("验证码生成失败", e);
        }
    }

    @Override
    public boolean validate(final String captchaId, final String userInput) {
        if (captchaId == null || userInput == null) {
            return false;
        }
        final String storedCode = UserCache.of().authorize(captchaId, this.configCaptcha.forArguments()).get();
        if (storedCode == null) {
            return false;
        }
        final boolean valid = storedCode.equalsIgnoreCase(userInput.trim());
        if (valid) {
            this.invalidate(captchaId);
        }
        return valid;
    }

    @Override
    public void invalidate(final String captchaKey) {
        if (captchaKey == null) {
            return;
        }
        UserCache.of().authorizeKo(captchaKey, this.configCaptcha.forArguments());
    }

    // ---- 内部类：支持样式配置的 LineCaptcha ----

    private static class StyledLineCaptcha extends LineCaptcha {
        private final ConfigSecurityCaptcha.ConfigStyle style;

        StyledLineCaptcha(final int width, final int height,
                          final CodeGenerator generator, final int interfereCount,
                          final ConfigSecurityCaptcha.ConfigStyle style) {
            super(width, height, generator, interfereCount);
            this.style = Objects.isNull(style) ? new ConfigSecurityCaptcha.ConfigStyle() : style;
        }

        @Override
        public Image createImage(final String code) {
            final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            final Graphics2D g = ImgUtil.createGraphics(image, this.backgroundColor());
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                this.drawInterfere(g);
                this.drawString(g, code);
            } finally {
                g.dispose();
            }
            return image;
        }

        private Color backgroundColor() {
            return this.colorOf(this.style.getBackground(), new ConfigSecurityCaptcha.ConfigStyle().getBackground());
        }

        private void drawInterfere(final Graphics2D g) {
            if (this.interfereCount <= 0) {
                return;
            }
            final Composite previous = g.getComposite();
            final Float alpha = this.style.getInterfereAlpha();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                Math.max(0f, Math.min(1f, Objects.isNull(alpha) ? 0.35f : alpha))));
            final List<Color> palette = this.paletteOf(this.style.getInterfere(), new ConfigSecurityCaptcha.ConfigStyle().getInterfere());
            final ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < this.interfereCount; i++) {
                final int xs = random.nextInt(this.width);
                final int ys = random.nextInt(this.height);
                final int xe = Math.max(0, Math.min(this.width, xs + random.nextInt(-this.width / 3, this.width / 3 + 1)));
                final int ye = Math.max(0, Math.min(this.height, ys + random.nextInt(-this.height / 2, this.height / 2 + 1)));
                g.setColor(palette.isEmpty() ? this.backgroundColor() : palette.get(random.nextInt(palette.size())));
                g.drawLine(xs, ys, xe, ye);
            }
            g.setComposite(previous);
        }

        private void drawString(final Graphics2D g, final String code) {
            if (StrUtil.isBlank(code)) {
                return;
            }
            if (Objects.nonNull(this.textAlpha)) {
                g.setComposite(this.textAlpha);
            }
            g.setFont(this.font);
            final FontMetrics metrics = g.getFontMetrics();
            final int length = code.length();
            final int charWidth = this.width / length;
            final int baseline = (this.height - metrics.getHeight()) / 2 + metrics.getAscent() - 1;
            final List<Color> palette = this.paletteOf(this.style.getText(), new ConfigSecurityCaptcha.ConfigStyle().getText());
            final ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < length; i++) {
                final String current = String.valueOf(code.charAt(i));
                final int textWidth = metrics.stringWidth(current);
                final int x = i * charWidth + Math.max(2, (charWidth - textWidth) / 2);
                final int y = baseline + random.nextInt(-1, 2);
                g.setColor(palette.isEmpty() ? Color.BLACK : palette.get(random.nextInt(palette.size())));
                g.drawString(current, x, y);
            }
        }

        private Color colorOf(final String value, final String fallback) {
            final String normalized = StrUtil.blankToDefault(value, fallback).trim().split(",")[0].trim();
            return Color.decode(normalized);
        }

        private List<Color> paletteOf(final String value, final String fallback) {
            return Arrays.stream(StrUtil.blankToDefault(value, fallback).split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .map(Color::decode)
                .toList();
        }
    }
}
