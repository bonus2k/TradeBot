<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>

    <div class="mb-1">Регистрация</div>
    <#if message??>
    <div class="alert alert-danger my-3" role="alert">
        ${message}
    </div>
    </#if>
    <@l.login "/registration" "Отправить" true/>

</@c.page>