export function createReportIo(deps) {
  function bytesToBase64(bytes) {
    let binary = "";
    const chunkSize = 0x8000;
    for (let i = 0; i < bytes.length; i += chunkSize) {
      const chunk = bytes.subarray(i, i + chunkSize);
      binary += String.fromCharCode.apply(null, Array.from(chunk));
    }
    return btoa(binary);
  }

  function triggerDownload(filename, content, mimeType = "application/octet-stream") {
    const blob = content instanceof Blob ? content : new Blob([content], { type: mimeType });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  }

  async function writeBinaryReportViaTauri(filename, bytes) {
    const base64 = bytesToBase64(bytes);
    return deps.tryInvokeTauriCommand("write_report_binary", {
      filename,
      contentBase64: base64,
      content_base64: base64
    });
  }

  async function writeBinaryWithFallback({
    filename,
    bytes,
    mime,
    successPrefixEn,
    successPrefixDe,
    fallbackMsgEn,
    fallbackMsgDe
  }) {
    try {
      const writtenPath = await writeBinaryReportViaTauri(filename, bytes);
      if (writtenPath) {
        deps.showToast((deps.getLangCode() === "en" ? successPrefixEn : successPrefixDe) + writtenPath, "success");
        return;
      }
    } catch (err) {
      const message = String(err || "");
      if (message.includes("EXPORT_CANCELED")) {
        deps.showToast(deps.t("exportCanceled"), "info");
        return;
      }
      triggerDownload(filename, bytes, mime);
      deps.showToast(deps.getLangCode() === "en" ? fallbackMsgEn : fallbackMsgDe, "success");
      return;
    }

    triggerDownload(filename, bytes, mime);
    deps.showToast(deps.getLangCode() === "en" ? fallbackMsgEn : fallbackMsgDe, "success");
  }

  async function writeTextWithFallback({
    filename,
    content,
    mime,
    successPrefixEn,
    successPrefixDe,
    fallbackMsgEn,
    fallbackMsgDe
  }) {
    try {
      const writtenPath = await deps.tryInvokeTauriCommand("write_report_csv", { filename, content });
      if (writtenPath) {
        deps.showToast((deps.getLangCode() === "en" ? successPrefixEn : successPrefixDe) + writtenPath, "success");
        return;
      }
    } catch (err) {
      const message = String(err || "");
      if (message.includes("EXPORT_CANCELED")) {
        deps.showToast(deps.t("exportCanceled"), "info");
        return;
      }
      triggerDownload(filename, content, mime);
      deps.showToast(deps.getLangCode() === "en" ? fallbackMsgEn : fallbackMsgDe, "success");
      return;
    }

    triggerDownload(filename, content, mime);
    deps.showToast(deps.getLangCode() === "en" ? fallbackMsgEn : fallbackMsgDe, "success");
  }

  return {
    writeBinaryWithFallback,
    writeTextWithFallback
  };
}
