---
description: R2MO HFS/HStore/RFS usage rules for storage, transfer, range download, and provider implementation.
globs:
  - "r2mo-io/**/*.java"
  - "r2mo-io-local/**/*.java"
  - "r2mo-ams/**/*.java"
  - "r2mo-jce/**/*.java"
  - "r2mo-spring/**/*.java"
alwaysApply: false
---

# HFS, HStore, and RFS Usage Rules

Load this rule in `r2mo-rapid` when a task touches file storage, file transfer, chunked upload, range download, local IO providers, license IO storage, or framework storage SPI behavior.

## Ownership

- `r2mo-ams` owns shared IO contracts and models: `HStore`, `HStoreMeta`, `HTransfer`, `TransferRequest`, `TransferToken`, `StoreChunk`, `FileRange`.
- `r2mo-io` owns neutral IO facades and orchestration: `HFS`, `RFS`, abstract store behavior, transfer initialization, and provider-facing components.
- `r2mo-io-local` owns local filesystem implementation: `HStoreLocal`, local readers/writers, range reader, zip/binary helpers, and path-to-URL behavior.
- Spring or Vert.x modules may expose runtime adapters, but they should not redefine provider contracts.

## Source Anchors

- `io.r2mo.io.common.HFS`
- `io.r2mo.base.io.HStore`
- `io.r2mo.io.common.AbstractHStore`
- `io.r2mo.io.local.operation.HStoreLocal`
- `io.r2mo.io.common.RFS`
- `io.r2mo.io.local.service.LocalLargeService`
- `io.r2mo.base.io.modeling.StoreChunk`
- `io.r2mo.base.io.modeling.FileRange`

## Decision Rules

- Use `HFS` when caller code needs a framework-level facade for copy, move, mkdir, remove, read, write, YAML/JSON reads, key IO, or file size.
- Use `HStore` when implementing or changing storage-provider behavior.
- Use `RFS` and `HTransfer` for multipart upload, resume, chunk status, complete, cancel, and transfer-token persistence.
- Use `FileRange` and `HStore.inBinary(path, range, null)` for range downloads.
- Use `HStore.inBinary(Set<String>, ...)` for multi-file binary output where the provider can optimize or zip.

## HFS Rules

- `HFS.of()` wraps the current store from `SPI.SPI_IO.ioAction()`; do not instantiate `HStoreLocal` directly from caller code.
- Keep `HFS` as a thin facade. New behavior that depends on a provider belongs in `HStore` or a concrete provider implementation.
- Preserve the storage-first and classpath-fallback semantics of `HFS.inContent`.
- Avoid adding business-specific path policy to `HFS`.

## HStore Rules

- `HStore` is the provider contract for directory operations, file operations, URL conversion, typed reads, YAML/JSON parsing, binary reads, range reads, and key IO.
- Provider implementations must make `toURL(String)` reliable because standard `inStream(String)` depends on it.
- Missing streams may return `null` under current default behavior; callers must handle nullable reads.
- `File` and `Path` overloads are not guaranteed for network or distributed providers unless the provider overrides them.
- Keep bulk operations idempotent where possible: empty `rm`, `mkdir`, or `mv` inputs should not fail.
- Keep `pHome` path joining provider-aware and use `HUri.UT.resolve` semantics rather than manual string concatenation.

## RFS / Transfer Rules

- Use `RFS.initRequest` to create transfer sessions and tokens.
- Use `RFS.ioUploadChunk` for chunk writes and `RFS.completeUpload` for final assembly.
- Use `RFS.getUploadedChunks`, `getWaitingChunks`, `getUploadProgress`, and `isComplete` for status responses.
- Persist enough token configuration for session recovery when the upper layer uses cache as the fast path.
- Keep upload chunk state and final file assembly in IO/transfer services, not in controller code.

## Provider Implementation Rules

- Keep local filesystem operations in `r2mo-io-local` helpers such as local writer, reader, highway, range reader, and zip helpers.
- Add new protocol providers beside local provider modules rather than expanding business modules.
- If adding a remote provider, explicitly review methods that currently assume local `File` or `Path` support.
- Preserve range-read behavior for providers that support partial download.

## Downstream Zero Integration Notes

- Zero Ecotope `zero-exmodule-ambient` uses R2MO transfer abstractions for chunked upload sessions.
- Zero Ecotope `zero-exmodule-integration` uses `HFS` and `HStore` through `FsDefault` for local configured storage and range downloads.
- If a Zero task requires new storage backend behavior, first decide whether it belongs in R2MO provider code or in Zero `Fs` adapter code.
