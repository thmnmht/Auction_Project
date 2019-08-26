var stompClient = null;
var auctionId = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#chats").html("");
}


function connect() {
    var socket = new SockJS('/socket');
    //if you connect through WebSocket (without SockJS)
    // var socket = new WebSocket('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({auth: "Bearer " + $("#token").val()}, function (frame) {
        console.log(frame);
        setConnected(true);
        console.log('Connected: ' + frame);
    });
}

function initSubscribe() {
    console.log("try to subscribe /app");
    stompClient.subscribe('/app', function (auctionId) {
        showChat("someone enter in auction " + auctionId.body);
        $("#chatHeader").append("someone enter in auction " + auctionId.body + " ");
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

var lastSubId;

function join() {
    auctionId = $("#auctionId").val();
    $("#chatHeader").append(auctionId + " ");
    lastSubId = stompClient.subscribe('/auction/' + auctionId, function (greeting) {
        showChat(JSON.parse(greeting.body));
    });
}

function disjoin() {
    auctionId = $("#auctionId").val();
    $("#chatHeader").append(' Exit from' + auctionId + "! ");
    stompClient.unsubscribe(lastSubId);
    // auctionId = null;
}

function send() {
    stompClient.send("/app/bid", {}, JSON.stringify({
        'price': $("#message").val(),
        'auctionId': auctionId
    }));
}

function showChat(message) {
    console.log(message);
    $("#chats").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#join").click(function () {
        join();
    });
    $("#disjoin").click(function () {
        disjoin();
    });
    $("#init").click(function () {
        initSubscribe();
    });
    $("#send").click(function () {
        send();
    });
});