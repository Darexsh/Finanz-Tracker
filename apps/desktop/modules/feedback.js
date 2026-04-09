export function createFeedback(deps) {
  function showDialog({
    title = deps.getLangCode() === "en" ? "Info" : "Hinweis",
    message = "",
    mode = "alert",
    defaultValue = "",
    okText = "OK",
    cancelText = deps.getLangCode() === "en" ? "Cancel" : "Abbrechen",
    danger = false
  }) {
    return new Promise(resolve => {
      const { dialogOverlay, dialogTitle, dialogMessage, dialogInput, dialogOkBtn, dialogCancelBtn } = deps.elements;

      dialogTitle.textContent = title;
      dialogMessage.textContent = message;
      dialogOkBtn.textContent = okText;
      dialogCancelBtn.textContent = cancelText;

      dialogOkBtn.classList.remove("danger", "primary");
      dialogOkBtn.classList.add(danger ? "danger" : "primary");

      const isPrompt = mode === "prompt";
      const hasCancel = mode === "confirm" || mode === "prompt";

      dialogInput.classList.toggle("hidden", !isPrompt);
      dialogCancelBtn.classList.toggle("hidden", !hasCancel);
      dialogInput.value = defaultValue;

      dialogOverlay.classList.remove("hidden");
      dialogOverlay.setAttribute("aria-hidden", "false");

      if (isPrompt) {
        setTimeout(() => {
          dialogInput.focus();
          dialogInput.select();
        }, 0);
      } else {
        setTimeout(() => dialogOkBtn.focus(), 0);
      }

      const finish = result => {
        cleanup();
        dialogOverlay.classList.add("hidden");
        dialogOverlay.setAttribute("aria-hidden", "true");
        resolve(result);
      };

      const onOk = () => {
        if (mode === "prompt") finish(dialogInput.value);
        else finish(true);
      };

      const onCancel = () => {
        if (mode === "confirm") finish(false);
        else if (mode === "prompt") finish(null);
        else finish(true);
      };

      const onKeyDown = evt => {
        if (evt.key === "Escape" && hasCancel) {
          evt.preventDefault();
          onCancel();
        }
        if (evt.key === "Enter") {
          if (!isPrompt || document.activeElement === dialogInput) {
            evt.preventDefault();
            onOk();
          }
        }
      };

      const onOverlayClick = evt => {
        if (evt.target === dialogOverlay && hasCancel) onCancel();
      };

      const cleanup = () => {
        dialogOkBtn.removeEventListener("click", onOk);
        dialogCancelBtn.removeEventListener("click", onCancel);
        dialogOverlay.removeEventListener("click", onOverlayClick);
        document.removeEventListener("keydown", onKeyDown);
      };

      dialogOkBtn.addEventListener("click", onOk);
      dialogCancelBtn.addEventListener("click", onCancel);
      dialogOverlay.addEventListener("click", onOverlayClick);
      document.addEventListener("keydown", onKeyDown);
    });
  }

  function showToast(message, type = "success", durationMs = 2600) {
    const host = deps.elements.toastContainer;
    if (!host) return;

    const toast = document.createElement("div");
    toast.className = "toast " + type;
    toast.textContent = message;
    host.appendChild(toast);

    requestAnimationFrame(() => {
      toast.classList.add("show");
    });

    const remove = () => {
      toast.classList.remove("show");
      setTimeout(() => toast.remove(), 180);
    };

    setTimeout(remove, durationMs);
  }

  async function showInfo(message, title = (deps.getLangCode() === "en" ? "Info" : "Hinweis")) {
    await showDialog({ title, message, mode: "alert", okText: "OK" });
  }

  async function askConfirm(message, title = (deps.getLangCode() === "en" ? "Confirmation" : "Bestätigung"), danger = false) {
    return showDialog({
      title,
      message,
      mode: "confirm",
      okText: danger ? deps.t("delete") : (deps.getLangCode() === "en" ? "Confirm" : "Bestätigen"),
      cancelText: deps.getLangCode() === "en" ? "Cancel" : "Abbrechen",
      danger
    });
  }

  async function askText(message, title = (deps.getLangCode() === "en" ? "Input" : "Eingabe"), defaultValue = "") {
    return showDialog({
      title,
      message,
      mode: "prompt",
      defaultValue,
      okText: deps.t("save"),
      cancelText: deps.getLangCode() === "en" ? "Cancel" : "Abbrechen"
    });
  }

  return {
    showDialog,
    showToast,
    showInfo,
    askConfirm,
    askText
  };
}
