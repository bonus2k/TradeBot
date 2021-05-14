<#include "security.ftlh">
<#import "login.ftlh" as l>
<nav class="navbar fixed-top navbar-expand-lg navbar-light white scrolling-navbar">
    <div class="container">
        <ul class="navbar-nav d-flex flex-row">

                <a class="navbar-brand me-2" href="/">
                    <img
                            src="/static/img/favicon.svg"
                            height="32"
                            alt=""
                            loading="lazy"
                            style="margin-top: -1px;"
                    />Trade Bot</a>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">



                <li class="nav-item">
                    <a class="nav-link" href="/"><i class="far fa-home-lg-alt"></i> Главная</a>
                </li>
                <#if user??>
                    <li class="nav-item">
                        <a class="nav-link" href="/alert"><i class="far fa-bell"></i> Сигналы</a>
                    </li>
                    <#if isUser>
                        <li class="nav-item">
                            <a class="nav-link" href="/order"><i class="far fa-shopping-cart"></i> Ордера</a>
                        </li>
                    </#if>
                    <li class="nav-item">
                        <a class="nav-link" href="/user/trade"><i class="far fa-cog"></i> Торги</a>
                    </li>
                </#if>
                <#if isAdmin>
                    <li class="nav-item">
                        <a class="nav-link" href="/user"><i class="far fa-users-cog"></i> Пользователи</a>
                    </li>
                </#if>
        </ul>


        <div class="d-flex align-items-center">
            <button
                    class="navbar-toggler"
                    type="button"
                    data-toggle="collapse"
                    data-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent"
                    aria-expanded="false"
                    aria-label="Toggle navigation">
                <i class="fas fa-bars"></i>
            </button>
            <#if isUser || isAdmin>
                <div class="dropdown open right-container">
                    <a
                            class="btn btn-primary dropdown-toggle"
                            href="#"
                            id="navbarDropdownMenu"
                            role="navigation"
                            data-toggle="dropdown"
                            aria-expanded="false">
                        <i class="far fa-user"></i> ${name}
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a class="dropdown-item" href="/user/setpassword">
                                <i class="far fa-key"></i> Пароль</a>
                        </li>

                        <li>
                            <a class="dropdown-item" href="/billing">
                                <i class="far fa-wallet"></i> Счет</a>
                        </li>
                        <li>
                            <@l.logout/>
                        </li>
                    </ul>
                </div>
            <#else>
                <div>
                    <i class="far fa-user"></i> ${name}
                </div>
            </#if>

            <#--            <#if name !="Гость">-->
            <#--                <@l.logout/>-->
            <#--            </#if>-->
            <#if name =="Гость">
                <a class="btn btn-primary" data-bs-toggle="offcanvas" href="/login" role="link"
                   aria-controls="offcanvasExample">
                    Вход
                </a>
            </#if>
        </div>
    </div>
</nav>