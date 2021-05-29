<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>

<@c.page>


    <div class="mb-1"><h5>Список сигналов</h5></div>

    <div>
        <table class="table table-sm">
            <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Действие</th>
                <th scope="col">Валюта</th>
                <th scope="col">Цена</th>
                <th scope="col">PNL</th>
                <th scope="col">Оповещение</th>
                <th scope="col">Время</th>
            </tr>
            </thead>

            <tbody>
            <#list alerts.content as alert>
                <tr>
                    <td><b>${alert.id}</b></td>
                    <td><span>${alert.alert?upper_case}</span></td>
                    <td><span>${alert.symbol}</span></td>
                    <td><span>${alert.price}</span></td>
                    <td>
                        <#if alert.rate lt 0>
                            <span class="text-danger">
          <i class="fas fa-caret-down me-1"></i><span> ${alert.rate}%</span></span>
                        </#if>
                        <#if alert.rate gt 0>
                            <span class="text-success">
          <i class="fas fa-caret-up me-1"></i><span> ${alert.rate}%</span></span>
                        </#if>
                    </td>
                    <td><span>${alert.botName}</span></td>
                    <td><b>${alert.date?string["d MMM, HH:mm"]}</b></td>
                </tr>
            <#else>
                <div>
                    <b>Нет записей</b>
                </div>
            </#list>
            </tbody>
        </table>
    </div>
    <@p.pager url alerts />

</@c.page>