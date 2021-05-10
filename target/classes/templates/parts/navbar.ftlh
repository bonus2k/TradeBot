<#include "security.ftlh">
<#import "login.ftlh" as l>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/">Trade Bot</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>


    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link" href="/">Главная</a>
            </li>
            <#if user??>
                <li class="nav-item">
                    <a class="nav-link" href="/alert">Сигналы</a>
                </li>
                <#if isUser>
                    <li class="nav-item">
                        <a class="nav-link" href="/order">Ордера</a>
                    </li>
                </#if>
                <li class="nav-item">
                    <a class="nav-link" href="/user/trade">Торги</a>
                </li>
            </#if>
            <#if isAdmin>
                <li class="nav-item">
                    <a class="nav-link" href="/user">Пользователи</a>
                </li>
            </#if>
        </ul>

        <div class="navbar-text mr-3">
            <#if isUser || isAdmin><a class="nav-link" href="/user/setpassword">${name}</a>
                <#else>
                    ${name}
            </#if>
        </div>
        <#if name !="Гость"> <#if isUser>
            <a class="nav-link" href="/billing">
                <div class="navbar-text mr-3">Ваш баланс: ${balance!?string["0.####"]}</div>
            </a>
                </#if>
            <@l.logout/>
        </#if>

        <#if name =="Гость">
            <a class="btn btn-primary" data-bs-toggle="offcanvas" href="/login" role="link"
               aria-controls="offcanvasExample">
                Вход
            </a>
        </#if>

    </div>
</nav>