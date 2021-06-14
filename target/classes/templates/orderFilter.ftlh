<#import "parts/common.ftlh" as c>
<#--<#import "parts/orderFilter.ftlh" as o>-->
<#import "parts/pager.ftlh" as p>



<@c.page>
    <#if user??><div class="mb-1"><h5><b>${user.username!}</b></h5></div></#if>
    <div class="row-cols-md-3"><h5>Список исполненных ордеров
            <button type="button" class="btn-outline-dark btn-circle" data-toggle="collapse" data-target="#filter"><i class="fal fa-filter"></i>
            </button>
        </h5>
    </div>


    <div id="filter" class="collapse">
        <form method="post">

            <div class="row border border-secondary rounded g-2">

                <div class="p-2 input-group input-group-sm col-md-2">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="inputGroup-sizing-sm">Статус: </span>
                    </div>
                    <select class="form-control" name=selectStatus>
                        <option selected>${selectStatus!}</option>
                        <#list statusList as status>
                            <option value="${status}">${status}</option>
                        </#list>
                    </select>
                </div>

                <div class="p-2 input-group input-group-sm col-md-2">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="inputGroup-sizing-sm">Тип: </span>
                    </div>
                    <select class="form-control" name=selectType>
                        <option selected>${selectType!}</option>
                        <#list typeList as type>
                            <option value="${type}">${type}</option>
                        </#list>
                    </select>
                </div>

                <div class="p-2 input-group input-group-sm col-md-2">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="inputGroup-sizing-sm">Операция: </span>
                    </div>
                    <select class="form-control" name=selectSide>
                        <option selected>${selectSide!}</option>
                        <option value="BUY">BUY</option>
                        <option value="SELL">SELL</option>
                    </select>
                </div>

                <div class="p-2 input-group input-group-sm col-md-2">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="inputGroup-sizing-sm">Валюта: </span>
                    </div>
                    <select class="form-control" name=selectSymbol>
                        <option selected>${selectSymbol!}</option>
                        <#list symbols as symbol>
                            <option value="${symbol}">${symbol}</option>
                        </#list>
                    </select>
                </div>




                <div class="p-2 input-group-sm input-daterange col-md-4">
                    <div class="input-group input-group-sm">
                        <div class="input-group-prepend">
                            <span class="input-group-text" id="inputGroup-sizing-sm">Период: </span>
                        </div>
                        <input type="text" id="dateCustom" class="form-control" placeholder="дата начала"
                               aria-label="дата начала" name=selectStart value="${selectStart!}">
                        <input type="text" id="dateCustom" class="form-control" placeholder="дата окончания"
                               aria-label="дата окончания" name=selectStop value="${selectStop!}">
                    </div>
                </div>

                <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                <input type="hidden" name="selectStatus" value="${selectStatus!}"/>
                <input type="hidden" name="selectType" value="${selectType!}"/>
                <input type="hidden" name="selectSymbol" value="${selectSymbol!}"/>
                <input type="hidden" name="selectSide" value="${selectSide!}"/>
                <input type="hidden" name="selectStart" value="${selectStart!}"/>
                <input type="hidden" name="selectStop" value="${selectStop!}"/>

                <div class="input-group-sm col-md-9">
                </div>

                <div class="p-2 input-group-sm col-md-3">
                    <button class="form-control">Поиск</button>
                </div>

            </div>

        </form>

    </div>


    <div>
        <table class="table table-sm table-responsive-sm">
            <thead>
            <tr>
                <th scope="col"><a href="${url}?sort=orderId&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> ID <a href="${url}?sort=orderId&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=price&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Курс <a href="${url}?sort=price&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=executedQty&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Объем <a href="${url}?sort=executedQty&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=status&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Статус <a href="${url}?sort=status&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=type&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Тип <a href="${url}?sort=type&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=side&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Операция <a href="${url}?sort=side&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=sum&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Сумма <a href="${url}?sort=sum&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=profit&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> PNL <a href="${url}?sort=profit&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=symbol&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Валюта <a href="${url}?sort=symbol&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=time&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a> Время <a href="${url}?sort=time&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a></th>
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
        Всего найдено записей: ${total!}
        PNL: ${sumProfit!}
    </div>
    <@p.pager url orders direction request/>

</@c.page>