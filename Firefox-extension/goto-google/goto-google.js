// about:debugging

//document.body.style.border = "5px solid red";
//alert("aaa");


// アクション内容
function openPage() {
    browser.tabs.create({
        url: "https://www.google.co.jp/"
    });
}


// ショートカットキーを押された時のハンドラ
browser.commands.onCommand.addListener(function(command) {
    if (command == "toggle-feature") {
        openPage();
    }
});

// ツールバー上のボタンをクリックされた時のハンドラ
browser.browserAction.onClicked.addListener(openPage);
