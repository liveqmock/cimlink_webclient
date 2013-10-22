$(document).ready(function() {
    Validate();
});
function PayVOUCHER0() {
    var ok = true;
    var challenge = $("input#recaptcha_challenge_field").val();
    var captcha = $("input#recaptcha_response_field").val();
    var voucher = $("input#voucher").val();
    if ((voucher.length != 19)) {
        ok = false;
        $('#messageVOUCHER').text("Voucher has to be 19 digits.");
        $('#messageVOUCHER').css("color", 'red');
    }
    var dataStringVOUCHER0 = 'type=VOUCHER&txid=www.f9ef53eea70d46ec9c082a03d&challenge==' + challenge + '&captcha=' + captcha + '&voucher=' + voucher;
    if (ok) {
        $.mobile.showPageLoadingMsg();
        $.ajax({type: "POST", url: "https://my.trustpay.biz/TrustPayWebClient/Pay?" + dataStringVOUCHER0 + "", }).done(function(msg) {
            $.mobile.hidePageLoadingMsg();
            if (msg.substring(0, 4) == ("http")) {
                window.location = msg;
            } else {
                $('#messageVOUCHER').text(msg);
                $('#messageVOUCHER').css("color", 'red');
            }
        });
    }
}
function PayCREDIT_CARD0() {
    var ok = true;
    var holder = $("input#holder").val();
    if ((holder.length < 1)) {
        ok = false;
        $('#messageCREDIT_CARD').text("Card Holder cannot be blank.");
        $('#messageCREDIT_CARD').css("color", 'red');
    }
    var number = $("input#number").val();
    if ((number.length < 15 || number.length > 16)) {
        ok = false;
        $('#messageCREDIT_CARD').text("Card number incorrect length.");
        $('#messageCREDIT_CARD').css("color", 'red');
    }
    var verifi = $("input#verifi").val();
    if ((verifi.length < 1)) {
        ok = false;
        $('#messageCREDIT_CARD').text("CVV cannot be blank.");
        $('#messageCREDIT_CARD').css("color", 'red');
    }
    var brand = $("input#brand").val();
    var expmonth = $("select#expmonth").val();
    var expyear = $("select#expyear").val();
    var given = $("input#given").val();
    var family = $("input#family").val();
    var email = $("input#email").val();
    var street = $("input#street").val();
    var city = $("input#city").val();
    var country = $("select#country").val();
    var zip = $("input#zip").val();
    var dataStringCREDIT_CARD0 = 'type=CREDIT CARD&txid=www.f9ef53eea70d46ec9c082a03d&holder=' + holder + '&number=' + number + '&verifi=' + verifi + '&brand=' + brand + '&expmonth=' + expmonth + '&expyear=' + expyear + '&given=' + given + '&family=' + family + '&email=' + email + '&street=' + street + '&city=' + city + '&country=' + country + '&zip=' + zip;
    if (ok) {
        $.mobile.showPageLoadingMsg();
        $.ajax({type: "POST", url: "https://my.trustpay.biz/TrustPayWebClient/Pay?" + dataStringCREDIT_CARD0 + "", }).done(function(msg) {
            $.mobile.hidePageLoadingMsg();
            if (msg.substring(0, 4) == ("http")) {
                window.location = msg;
            } else {
                $('#messageCREDIT_CARD').text(msg);
                $('#messageCREDIT_CARD').css("color", 'red');
            }
        });
    }
}
function PayCIMS0() {
    var ok = true;
    var msisdn = $("input#msisdn").val();
    var dataStringCIMS0 = 'type=CIMS&txid=www.f9ef53eea70d46ec9c082a03d&msisdn=' + msisdn;
    if (ok) {
        $.mobile.showPageLoadingMsg();
        $.ajax({type: "POST", url: "https://my.trustpay.biz/TrustPayWebClient/Pay?" + dataStringCIMS0 + "", }).done(function(msg) {
            $.mobile.hidePageLoadingMsg();
            if (msg.substring(0, 4) == ("http")) {
                window.location = msg;
            } else {
                $('#messageCIMS').text(msg);
                $('#messageCIMS').css("color", 'red');
            }
        });
    }
}
function PayMobile() {
    window.location = "https://my.trustpay.biz";
}
function Validate() {
    $("input#number").on("input", null, null, function() {
        var cc = $("input#number").val();
        if (cc.substring(0, 1) == ("4")) {
            $("input#brand").val("VISA");
            $("img#brandimg").attr("src", "images/visa.png");
        } else if (cc.substring(0, 2) == ("51") || cc.substring(0, 2) == ("52") || cc.substring(0, 2) == ("53") || cc.substring(0, 2) == ("54") || cc.substring(0, 2) == ("55")) {
            $("input#brand").val("MASTER");
            $("img#brandimg").attr("src", "images/mastercard.png");
        } else {
            $("input#brand").val("NONE");
            $("img#brandimg").attr("src", "images/creditcard.png");
        }
    })
}