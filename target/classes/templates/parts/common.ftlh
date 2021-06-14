<#macro page>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <meta http-equiv="x-ua-compatible" content="ie=edge"/>
    <title>Trade Bot</title>
    <!-- Fav icon -->
    <meta name = "theme-color" content = "# ffffff">
    <link rel = "icon" href = "static/img/favicon.svg">
    <link rel = "mask-icon" href = "static/img/mask-icon.svg" color = "# 000000">
    <link rel = "apple-touch-icon" href = "static/img/apple-touch-icon.png">
    <link rel = "manifest" href = "static/manifest.json">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="/static/css/all.min.css">
    <!-- Google Fonts Roboto -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap">
    <!-- Material Design Bootstrap -->
    <link rel="stylesheet" href="/static/css/mdb.min.css">
    <!-- Your custom styles (optional) -->
    <link rel="stylesheet" href="/static/css/style.css">
    <!--Google recaptcha-->
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>


</head>
<body class="grey lighten-3">
<#include "navbar.ftlh">
<div class="container mt-5">
    <#nested>
</div>
<a class="showButtonUp" id="buttonUp"></a>
<!-- jQuery -->
<script type="text/javascript" src="/static/js/jquery.min.js"></script>

<!-- Bootstrap tooltips -->
<script type="text/javascript" src="/static/js/popper.min.js"></script>
<!-- Bootstrap core JavaScript -->
<script type="text/javascript" src="/static/js/bootstrap.min.js"></script>
<!-- Bootstrap core CSS -->
<link rel="stylesheet" href="/static/css/bootstrap.min.css">
<!-- MDB core JavaScript -->
<script type="text/javascript" src="/static/js/mdb.min.js"></script>
<#--bootstrap-datepicker.readthedocs.io-->
<script src="/static/js/bootstrap-datepicker.min.js"></script>
<script src="/static/js/bootstrap-datepicker.ru.min.js"></script>
<link rel="stylesheet" href="/static/css/bootstrap-datepicker.min.css">
<!-- Your custom scripts (optional) -->
<script type="text/javascript">


    $(document).ready(function(){

        $('.input-daterange').datepicker({
                format: "dd/mm/yyyy",
                autoclose: true,
                todayHighlight: true,
                language: "ru",
                daysOfWeekHighlighted: "0,6"
        });

    });

    jQuery(document).ready(function() {
        $(window).scroll(function () {
            if ($(this).scrollTop() > 100) {
                $('#buttonUp').fadeIn();
            } else {
                $('#buttonUp').fadeOut();
            }
        });
        $('#buttonUp').on('click', function(e) {
            e.preventDefault();
            $('html, body').animate({scrollTop:0}, '500');
        });
    });


    function postToUrl(path, params, method) {
        method = method || "post";

        var form = document.createElement("form");
        form.setAttribute("method", method);
        form.setAttribute("action", path);
        for(var key in params) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }

        document.body.appendChild(form);
        form.submit();
    }
</script>

</body>
</html>
</#macro>