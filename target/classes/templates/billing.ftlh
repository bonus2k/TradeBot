<#import "parts/common.ftlh" as c>
<#import "parts/billing.ftlh" as b>
<#include "parts/security.ftlh">
<#setting locale="en_US">

<@c.page>

    <div>
    <div class="form-group row">
        <div class="col-sm-2 col-form-label"><h5><b>${userDB.username}</b></h5></div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Средства на бирже:</label>
            <div class="col-sm-6">
                <label class="col-sm-2 col-form-label"><b> ${userDB.amount!} USDT</b></label>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Доход за неделю:</label>
            <div class="col-sm-6">
                <label class="col-sm-2 col-form-label">${userBilling.profitOnWeek!"0"} USDT</label>
            </div>
        </div>
    <#if isAdmin>
        <form method="post">

            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Баланс:</label>
                <div class="col-sm-6">
                    <input type="text" name="balance"
                           class="form-control" value="${userBilling.balance}">
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Процент:</label>
                <div class="col-sm-6">
                    <input type="text" name="rate"
                           class="form-control" value="${userBilling.rate}">
                </div>
            </div>


            <div class="form-group row">
                <label for="exampleFormControlTextarea1" class="col-sm-2 col-form-label">Коментарии:</label>
                <div class="col-sm-6">
            <textarea class="form-control" id="exampleFormControlTextarea1" rows="3"
                      name="comment">${userBilling.comment!""}</textarea>
                </div>
            </div>

            <input type="hidden" name="_csrf" value="${_csrf.token}">

            <div class="col-md-7 mb-3 mt-2">
                <button type="submit" class="btn btn-primary">Сохранить</button>
            </div>

        </form>
    </div>
    </#if>
    <div class="mb-1"><h5>Список счетов</h5></div>
    <@b.billing>

    </@b.billing>
</@c.page>