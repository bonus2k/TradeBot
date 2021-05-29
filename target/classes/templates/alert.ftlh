<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>

<@c.page>


    <div class="mb-1"><h5>Список сигналов</h5></div>

    <div>
        <table class="table table-sm">
            <thead>
            <tr>
                <th scope="col">ID <a href="/alert?sort=id"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Действие <a href="/alert?sort=alert"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Валюта <a href="/alert?sort=symbol"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Цена <a href="/alert?sort=price"><i class="fal fa-sort"></i></a></th>
                <th scope="col">PNL <a href="/alert?sort=rate"><i class="fal fa-sort"></i></a></th>
                <th scope="col">Оповещение <a href="/alert?sort=botName"> <i class="fal fa-sort"></i></a></th>
                <th scope="col">Время <a href="/alert?sort=date"><i class="fal fa-sort"></i></a></th>
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