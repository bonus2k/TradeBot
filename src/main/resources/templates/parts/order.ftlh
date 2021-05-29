<#import "pager.ftlh" as p>
<#import "pager.ftlh" as p>
<#macro order url>
    <div>
        <table class="table table-sm">
            <thead>
            <tr>
                <th scope="col">ID <a href="/${url}?sort=orderId"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Курс  <a href="/${url}?sort=price"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Объем  <a href="/${url}?sort=executedQty"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Статус  <a href="/${url}?sort=status"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Тип  <a href="/${url}?sort=type"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Операция  <a href="/${url}?sort=side"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Сумма  <a href="/${url}?sort=sum"><i class="fal fa-sort"></i></a></th>
                <th scope="col">PNL  <a href="/${url}?sort=profit"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Валюта <a href="/${url}?sort=symbol"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Время  <a href="/${url}?sort=time"><i class="fal fa-sort"></i></a></th>
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