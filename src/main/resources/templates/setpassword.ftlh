<#import "parts/common.ftlh" as c>
<#include "parts/security.ftlh">
<#import "parts/billing.ftlh" as b>
<@c.page>

    <#if message??>
        <div class="alert alert-danger my-3 w-50" role="alert">
            ${message}
        </div>
    </#if>

    <form method="post">
        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Пароль:</label>
            <div class="col-sm-6">
                <input type="password" name="password"
                       class="form-control ${(passwordError??)?string('is-invalid', '')}" value="${userDB.password}">
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Пароль:</label>
            <div class="col-sm-6">
                <input type="password" name="confirmPassword"
                       class="form-control ${(password2Error??)?string('is-invalid', '')}"
                value="${userDB.password}">
                <#if password2Error??>
                    <div class="invalid-feedback">
                        ${password2Error}
                    </div>
                </#if>
            </div>
        </div>

        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <input type="hidden" name="username" value="${userDB.username}"/>
        <input type="hidden" name="email" value="${userDB.email}"/>
        <div class="col-auto my-3">
        <button class="btn btn-primary ">Сохранить</button>
        </div>

    </form>

</@c.page>