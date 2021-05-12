<#import "parts/common.ftlh" as c>

<@c.page>


    <div class="mb-1"><h5>Список сигналов</h5></div>

    <div>
    <table class="table">
    <thead>
    <tr>
        <th scope="col">ID</th>
        <th scope="col">Действие</th>
        <th scope="col">Валюта</th>
        <th scope="col">Цена</th>
        <th scope="col">Доходность</th>
        <th scope="col">Оповещение</th>
        <th scope="col">Время</th>
    </tr>
    </thead>

    <tbody>
    <#list alerts as alert>
<tr>
        <td><b>${alert.id}</b></td>
        <td><span>${alert.alert}</span></td>
        <td><span>${alert.symbol}</span></td>
        <td><span>${alert.price}</span></td>
        <td><span>${alert.rate}</span></td>
        <td><span>${alert.botName}</span></td>
        <td><b>${alert.date?string["MMM d, HH:mm"]}</b></td>
</tr>
    <#else>
        No alerts
    </#list>
        </tbody>
        </table>
        </div>


</@c.page>