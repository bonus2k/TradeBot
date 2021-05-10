<#import "parts/common.ftlh" as c>
<#include "parts/security.ftlh">
<@c.page>
<#--    <h5>${userDB.username}</h5>-->
    <#if message??>
        <div class="alert alert-danger my-3" role="alert">
            ${message}
        </div>
    </#if>

    <form method="post">

        <#if isUser>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">API Key:</label>
                <div class="col-sm-6">
                    <input type="text" name="key" class="form-control" placeholder="API Key" value="${userDB.key!''}"/>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Secret Key:</label>
                <div class="col-sm-6">
                    <input type="password" name="secret" class="form-control" placeholder="Secret Key"/>
                </div>
            </div>
            <div class="col-auto my-3">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="autoSizingCheck2"
                           name="isCanTrade" ${userDB.isCanTrade()?string("checked", "")}/>
                    <label class="form-check-label"
                           for="autoSizingCheck2">Ключи валидные</label>
                </div>
            </div>
        </#if>

        <#if isAdmin>
            <div class="col-auto my-3">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="autoSizingCheck2"
                           name="tradeIsEnable" ${tradeIsEnable?string("checked", "")}/>
                    <label class="form-check-label"
                           for="autoSizingCheck2">Торги разрешены</label>
                </div>
            </div>
        </#if>

        <#if isAdmin || userDB.isCanTrade()>
            <div class="col-auto my-3">Отметьте пары, по которым будут проводиться торги</div>

            <div class="container">
                <div class="row">
                    <#list symbols as symbol>
                        <div class="col-md-4">
                            <div class="p-3 border border-primary rounded my-3 .col-6 .col-md-4">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" id="autoSizingCheck2"
                                           name=${symbol} ${userDB.getSymbol()?seq_contains(symbol)?string("checked", "")}>

                                    <label class="form-check-label"
                                           for="autoSizingCheck2"> ${symbol}</label>
                                   <#if isAdmin><a href="/handler?sym=${symbol}">SELL</a></#if>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </#if>

        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <div class="col-auto my-3">
            <button class="btn btn-primary ">Сохранить</button>
        </div>

    </form>
</@c.page>