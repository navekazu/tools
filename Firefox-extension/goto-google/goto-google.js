// about:debugging

//document.body.style.border = "5px solid red";
//alert("aaa");


// �A�N�V�������e
function openPage() {
    browser.tabs.create({
        url: "https://www.google.co.jp/"
    });
}


// �V���[�g�J�b�g�L�[�������ꂽ���̃n���h��
browser.commands.onCommand.addListener(function(command) {
    if (command == "toggle-feature") {
        openPage();
    }
});

// �c�[���o�[��̃{�^�����N���b�N���ꂽ���̃n���h��
browser.browserAction.onClicked.addListener(openPage);
