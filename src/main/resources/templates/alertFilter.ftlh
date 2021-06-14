<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>
<#include "parts/security.ftlh">

<@c.page>


    <div class="row-cols-md-3">
        <h5>Список сигналов
            <button type="button" class="btn-outline-dark btn-circle" data-toggle="collapse" data-target="#filter"><i
                        class="fal fa-filter"></i>
            </button>
        </h5>
    </div>



    <div id="filter" class="collapse">
        <form method="post">
            <div class="row border border-secondary rounded g-2">

                <div class="p-2 input-group input-group-sm col-md-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="inputGroup-sizing-sm">Операция: </span>
                    </div>
                    <select class="form-control" name=selectSide>
                        <option selected>${selectSide!}</option>
                        <option value="BUY">BUY</option>
                        <option value="SELL">SELL</option>
                    </select>
                </div>

                <div class="p-2 input-group input-group-sm col-md-3">
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
                <input type="hidden" name="selectSymbol" value="${selectSymbol!}"/>
                <input type="hidden" name="selectSide" value="${selectSide!}"/>
                <input type="hidden" name="selectStart" value="${selectStart!}"/>
                <input type="hidden" name="selectStop" value="${selectStop!}"/>

                <#--            <div class="input-group-sm col-md-9">-->
                <#--            </div>-->

                <div class="p-2 input-group-sm col-md-2">
                    <button class="form-control">Поиск</button>
                </div>


            </div>

        </form>

    </div>


    <div>
        <table class="table table-sm table-responsive-sm">
            <thead>
            <tr>
                <th scope="col"><a href="${url}?sort=id&direc=desc${request!}"><i class="fal fa-sort-numeric-down"></i></a>
                    ID <a
                            href="${url}?sort=id&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a>
                </th>
                <th scope="col"><a href="${url}?sort=alert&direc=desc${request!}"><i
                                class="fal fa-sort-numeric-down"></i></a>
                    Операция <a href="${url}?sort=alert&direc=asc${request!}"><i
                                class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=symbol&direc=desc${request!}"><i
                                class="fal fa-sort-numeric-down"></i></a>
                    Валюта <a href="${url}?sort=symbol&direc=asc${request!}"><i
                                class="fal fa-sort-numeric-down-alt"></i></a></th>
                <th scope="col"><a href="${url}?sort=price&direc=desc${request!}"><i
                                class="fal fa-sort-numeric-down"></i></a> Курс
                    <a href="${url}?sort=price&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a>
                </th>
                <th scope="col"><a href="${url}?sort=rate&direc=desc${request!}"><i
                                class="fal fa-sort-numeric-down"></i></a> PNL
                    <a href="${url}?sort=rate&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a>
                </th>
                <#if isAdmin>
                    <th scope="col"><a href="${url}?sort=botName&direc=desc${request!}"><i
                                    class="fal fa-sort-numeric-down"></i></a> Оповещение <a
                                href="${url}?sort=botName&direc=asc${request!}"><i
                                    class="fal fa-sort-numeric-down-alt"></i></a>
                    </th>
                </#if>
                <th scope="col"><a href="${url}?sort=date&direc=desc${request!}"><i
                                class="fal fa-sort-numeric-down"></i></a> Время
                    <a href="${url}?sort=date&direc=asc${request!}"><i class="fal fa-sort-numeric-down-alt"></i></a>
                </th>
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
                    <#if isAdmin>
                        <td><span>${alert.botName}</span></td>
                    </#if>
                    <td><b>${alert.date?string["d MMM, HH:mm"]}</b></td>
                </tr>
            <#else>
                <div>
                    <b>Нет записей</b>
                </div>
            </#list>
            </tbody>
        </table>
        Всего найдено записей: ${total!}
    </div>

    <@p.pager url alerts direction request/>

</@c.page>