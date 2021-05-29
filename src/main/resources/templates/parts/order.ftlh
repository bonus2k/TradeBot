<#import "pager.ftlh" as p>
<#import "pager.ftlh" as p>
<#macro order>
    <div>
        <table class="table table-sm">
            <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Курс</th>
                <th scope="col">Объем</th>
                <th scope="col">Статус</th>
                <th scope="col">Тип</th>
                <th scope="col">Операция</th>
                <th scope="col">Сумма</th>
                <th scope="col">PNL</th>
                <th scope="col">Валюта</th>
                <th scope="col">Время</th>
            </tr>
            </thead>

            <tbody>
            <#list orders.content as order>
                <tr>

                    <td><b>${order.orderId?string["##########"]}</b></td>
                    <td><span>${order.price?remove_ending("000")}</span></td>
                    <td><span>${order.executedQty?remove_ending("000")}</span></td>
                    <td><span>${order.status}</span></td>
                    <td><span>${order.type}</span></td>
                    <td><span>${order.side}</span></td>
                    <td><span>${order.sum}</span></td>
                    <td><#if order.profit lt 0>
                            <span class="text-danger">
                                <span> ${order.profit} <i class="far fa-usd-circle"></i></span></span>
                        </#if>
                        <#if order.profit gt 0>
                            <span class="text-success">
                                <span>${order.profit} <i class="far fa-usd-circle"></i></span></span>
                        </#if>
                    </td>
                    <td><span>${order.symbol}</span></td>
                    <td><b>${order.time?string["d MMM, HH:mm"]}</b></td>

                </tr>
            <#else>
                <div>
                    <b>Нет записей</b>
                </div>
            </#list>
            </tbody>
        </table>
    </div>
    <@p.pager url orders />
</#macro>