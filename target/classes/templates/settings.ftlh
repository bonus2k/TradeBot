<#import "parts/common.ftlh" as c>
<#setting locale="en_US">
<@c.page>

    <form method="post">
        <div class="form-group row">
            <label class="col-auto col-form-label">Лимитный ордер:</label>
            <div class="row-cols-sm-3">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="autoSizingCheck2"
                           name="limit" ${symbolUsr.limit!?string("checked", "")}>
                </div>
            </div>
        </div>
            <div class="form-group row">
                <label class="col-auto col-form-label">Процент:</label>
                <div class="row-cols-sm-3">
                    <input type="text" class="form-control" name="percent"
                           value="${symbolUsr.percent}">
                </div>
            </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <input type="hidden" name="symbol" value="${symbol}"/>
        <div class="col-auto my-3">
            <button class="btn btn-primary ">Сохранить</button>
        </div>
        </div>
    </form>
</@c.page>