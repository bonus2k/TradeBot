<#macro login path button isRegisterForm>
    <form action="${path}" method="post">

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Логин:</label>
            <div class="col-sm-6">
                <input type="text" name="username" class="form-control ${(usernameError??)?string('is-invalid', '')}"
                       placeholder="Введите логин (от 5 до 24 символов)"
                        <#if isRegisterForm && username??> value=${username} </#if>
                >
                <#if usernameError??>
                    <div class="invalid-feedback">
                        ${usernameError}
                    </div>
                </#if>

            </div>
        </div>
        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Пароль:</label>
            <div class="col-sm-6">
                <input type="password" name="password"
                       class="form-control ${(passwordError??)?string('is-invalid', '')}" placeholder="Введите пароль">
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
        </div>
        <#if isRegisterForm>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Пароль:</label>
                <div class="col-sm-6">
                    <input type="password" name="confirmPassword"
                           class="form-control ${(password2Error??)?string('is-invalid', '')}"
                           placeholder="Повторите пароль">
                    <#if password2Error??>
                        <div class="invalid-feedback">
                            ${password2Error}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Email:</label>
                <div class="col-sm-6">
                    <input type="email" name="email" class="form-control ${(emailError??)?string('is-invalid', '')}"
                           placeholder="example@email.com"
                            <#if isRegisterForm && email??> value=${email} </#if>>
                    <#if emailError??>
                        <div class="invalid-feedback">
                            ${emailError}
                        </div>
                    </#if>
                </div>
            </div>

            <div class="my-3">
                <div class="g-recaptcha" data-sitekey="6LctH68aAAAAAMiNxwqUUNvK1PdH6pB0DiPlDIFd"></div>
                <#if captchaError??>
                    <div class="alert alert-danger my-3" role="alert">
                        ${captchaError}
                    </div>
                </#if>
            </div>
        </#if>
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>

        <button class="btn btn-primary ">${button}</button>
        <#if !isRegisterForm>
            <a href="/registration" class="btn btn-outline-primary" role="button" aria-pressed="true">Регистрация</a>
        </#if>
    </form>
</#macro>

<#macro logout>
    <form action="/logout" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <button class="btn btn-primary">Выход</button>
    </form>
</#macro>