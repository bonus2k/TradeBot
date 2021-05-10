<#macro billing>
    <#setting locale="ru_RU">
    <div>
        <table class="table">
            <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Баланс</th>
                <th scope="col">Доход за неделю</th>
                <th scope="col">Процент</th>
                <th scope="col">Дата</th>
                <th scope="col">Комментарий</th>

            </tr>
            </thead>

            <tbody>
            <#list billings as billing>
                <tr>

                    <td><b>${billing.id}</b></td>
                    <td><span>${billing.balance} USDT</span></td>
                    <td><span>${billing.profitOnWeek!?string["0.####"]} USDT</span></td>
                    <td><span>${billing.rate}%</span></td>
                    <td><b>${billing.date?string["MMM d, HH:mm"]}</b></td>
                    <td><span>${billing.comment!}</span></td>
                </tr>
            <#else>
                No alerts
            </#list>
            </tbody>
        </table>
    </div>
</#macro>