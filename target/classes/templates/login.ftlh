<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<@c.page>
    <div>
        <div class="mb-1">Авторизация</div>
        <#if Session?? && Session.SPRING_SECURITY_LAST_EXCEPTION??>
            <div class="alert alert-danger my-3" role="alert">
                ${(SPRING_SECURITY_LAST_EXCEPTION.message??)?string("Неправильный логин или пароль", "")}
            </div>
        </#if>
        <#if message??>
            <div class="alert alert-${messageType} my-3" role="alert">
                ${message}
            </div>
        </#if>
        <@l.login "/login" "Войти" false/>

    </div>
</@c.page>