<#macro order>
    <div>
        <table class="table">
            <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Курс</th>
                <th scope="col">Объем</th>
                <th scope="col">Операция</th>
                <th scope="col">Сумма</th>
                <th scope="col">Доход</th>
                <th scope="col">Валюта</th>
                <th scope="col">Время</th>
            </tr>
            </thead>

            <tbody>
            <#list orders as order>
                <tr>

                    <td><b>${order.id}</b></td>
                    <td><span>${order.price}</span></td>
                    <td><span>${order.quantity?string["0.#######"]}</span></td>
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
                    <td><b>${order.timestamp?string["MMM d, HH:mm"]}</b></td>

                </tr>
            <#else>
                No alerts
            </#list>
            </tbody>
        </table>
    </div>
</#macro>