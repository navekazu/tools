// editorの高さを設定する関数
function applyEditorResizing() {
  const interval = setInterval(() => {
    const div = document.querySelector('#descriptionTextArea');
    if (div) {
      console.log("editor 見つかったので高さを変更");
      div.style.height = "500px"; // お好みで変更可能
      div.style.minHeight = "500px";
      clearInterval(interval);
    }
  }, 300);
}

// SPA対応：履歴APIをフック
function hookSPA(callback) {
  const origPush = history.pushState;
  const origReplace = history.replaceState;

  history.pushState = function () {
    origPush.apply(this, arguments);
    callback();
  };
  history.replaceState = function () {
    origReplace.apply(this, arguments);
    callback();
  };
  window.addEventListener('popstate', callback);
}

// 初回実行（リロード対策）
applyEditorResizing();

// URL変化を監視して再度リサイズ
hookSPA(() => {
  console.log("URL変更（SPA遷移）検出: " + location.href);
  setTimeout(applyEditorResizing, 500); // 遷移後のDOM構築を待つ
});