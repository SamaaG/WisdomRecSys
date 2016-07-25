<%--
  Created by IntelliJ IDEA.
  User: dwk89
  Date: 07/24/2016
  Time: 02:49:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Wisdom Restaurant Recommendation System</title>
</head>
<style>
    body {
        background-color: beige;
    }
    #systemName {
        font-family: "Trebuchet MS", sans-serif;
        color: firebrick;
        text-align: center;
        font-size: 24px;
        font-weight: bold;
        text-decoration:underline;
    }
    #classTitle {
        font-family: "Trebuchet MS", sans-serif;
        color: blueviolet;
        text-align: center;
        font-size: 12pt;
        font-weight: normal;
    }
    #logo {
        display: block;
        margin: auto;
    }
    #studentName {
        font-family: "Trebuchet MS", sans-serif;
    }
    #instruction {
        font-family: "Trebuchet MS", sans-serif;
        color: black;
        font-size: 18px;
        text-align: center;
    }
    #checkBox {
        font-family: "Trebuchet MS", sans-serif;
        color: dodgerblue;
    }
    #d {
        vertical-align: middle;
    }
    #d2 {
        vertical-align: middle;
    }
    .button {
        display: inline-block;
        font-family: "Trebuchet MS", sans-serif;
        font-size: 16px;
        background-color: #f44336;
        border-radius: 12px;
        border: none;
        text-align: center;
        width: 200px;
        padding: 9px 10px;
        cursor: pointer;
        transition: all 0.5s;
    }
    .button span {
        cursor: pointer;
        display: inline-block;
        position: relative;
        transition: 0.5s;
    }
    .button span:after {
        content: '>>';
        position: absolute;
        opacity: 0;
        top: 0;
        right: -20px;
        transition: 0.5s;
    }
    .button:hover span {
        padding-right: 25px;
    }
    .button:hover span:after {
        opacity: 1;
        right: 0;
    }
    .button2 {
        display: inline-block;
        font-family: "Trebuchet MS", sans-serif;
        font-size: 16px;
        background-color: #e7e7e7;
        border-radius: 12px;
        border: none;
        text-align: center;
        width: 200px;
        padding: 9px 10px;
        cursor: pointer;
        transition: all 0.5s;
    }
    .button2 span {
        cursor: pointer;
        display: inline-block;
        position: relative;
        transition: 0.5s;
    }
    .button2 span:after {
        content: '>>';
        position: absolute;
        opacity: 0;
        top: 0;
        right: -20px;
        transition: 0.5s;
    }
    .button2:hover span {
        padding-right: 25px;
    }
    .button2:hover span:after {
        opacity: 1;
        right: 0;
    }
    input[type = text] {
        background-color: beige;
        background-image: url('http://imgh.us/searchDW.png');
        background-position: 2px 2px;
        background-size: 15px 15px;
        margin: 20px 0px 10px 0px;
        background-repeat: no-repeat;
        color: brown;
        font-family: "Trebuchet MS", sans-serif;
        font-size: 15px;
        padding-left: 20px;
        padding-right: 5px;
        width: 600px;
        border: 2px solid gray;
        border-radius: 4px;
    }
</style>

<body>
<p id = "classTitle">COMP-SCI 5560 (SS16) - Class Project</p>
<img id = "logo", src = "http://imgh.us/logo2_23.png", alt = "<ERROR>", style = "width: 54px; height: 54px;" />
<h1 id = "systemName">Wisdom Restaurant Recommendation System</h1>
<p><br></p>
<table id = "studentName", style = "width: 300px", align = "center">
    <tr>
        <td align = "center">Team <b>5</b>:</td>
        <td><i>Samaa Gazzaz (9)</i></td>
    </tr>
    <tr>
        <td></td>
        <td><i>Pooja Shekhar (38)</i></td>
    </tr>
    <tr>
        <td></td>
        <td><i>Chen Wang (44)</i></td>
    </tr>
    <tr>
        <td></td>
        <td><i>Dayu Wang (45)</i></td>
    </tr>
</table>
<p><br></p>
<p id = "instruction"><b>Please start your search here.</b></p>
<form id = "userSelection", action = "WisdomServlet", method = "get">
    <p align = "center"><input type = "text", name = "searchText", placeholder = "Look for a restaurant.."></p>
    <table id = "checkBox", styple = "width: 600px", align = "center", cellpadding = "10px">
        <tr>
            <td><br>1. Looking for a restaruant for?</td>
            <td><br><input type = "radio", name = "mealType", value = "breakfast"> Breakfast <input type = "radio", name = "mealType", value = "lunch"> Lunch <input type = "radio", name = "mealType", value = "dinner" checked> Dinner</td>
        </tr>
        <tr>
            <td><br>2. Do you have <b>COST EXPECTATION</b>?</td>
            <td><br><input type = "checkbox", name = "cost"> Price No-Higher Than <img id = "d", src = "http://imgh.us/dollar.png", alt = "<ERROR>", style = "width: 18px; height: 18px; margin: 0px 0px 3px 0px;" /><img id = "d2", src = "http://imgh.us/dollar.png", alt = "<ERROR>", style = "width: 18px; height: 18px; margin: 0px 0px 3px 0px;" /></td>
        </tr>
        <tr valign = "top">
            <td><br>5. Other kinds of service?</td>
            <td><br><input type = "checkbox", name = "wifi"> Complimentary Wi-Fi Service<br><input type = "checkbox", name = "calory"> Under-600-Calories <b>ENTREE</b> Avaliable</td>
        </tr>
    </table>
    <p id = "button", align = "center"><br><input type = "submit", value = "Submit"><a href="https://localhost:8080/results.html">test     test</a><button class = "button"><span>Find A Restaurant</span></button><button class = "button button2", style = "margin: 0px 0px 0px 16px;"><span>Start Over</span></button></p>
</form>
</body>
</html>