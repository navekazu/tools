(() => {
  const TARGET_ID = "descriptionTextArea";
  const HEIGHT = "600px";

  const isEditablePage = () =>
    location.pathname.includes("/edit") ||
    location.pathname.startsWith("/add/");

  const resize = () => {
    if (!isEditablePage()) return;

    const el = document.getElementById(TARGET_ID);
    if (!el) return;

    el.style.height = HEIGHT;
    el.style.maxHeight = HEIGHT;
    el.style.overflowY = "auto";
  };

  // URL + DOM 変更をまとめて監視
  const observer = new MutationObserver(() => {
    resize();
  });

  observer.observe(document.body, {
    childList: true,
    subtree: true
  });

  // 初期実行（/edit 直アクセス用）
  resize();
})();
