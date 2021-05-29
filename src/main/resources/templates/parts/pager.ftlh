<#macro pager url page>
    <#if page.getTotalPages() gt 5>
        <#assign
        totalPages = page.getTotalPages()
        pageNumber = page.getNumber() + 1
        head = (pageNumber > 2)?then([pageNumber-2, pageNumber-1],[])
        tail = (pageNumber < totalPages-1)?then([pageNumber+1, pageNumber+2],[])
        bodyBefore = (pageNumber = 2)?then([pageNumber-1], [])
        bodyAfter = (pageNumber = totalPages-1)?then([pageNumber+1], [])
        body = head + bodyBefore + [pageNumber] + bodyAfter+tail
        />
    <#else>
        <#assign
        totalPages = page.getTotalPages()
        pageNumber = page.getNumber() + 1
        body = 1..page.getTotalPages()/>
    </#if>
    <div class="mt-3">
        <ul class="pagination justify-content-center">
            <#if pageNumber gt 1>
                <li class="page-item">
                    <a class="page-link" href="${url}?page=0&size=${page.getSize()}" tabindex="-1"
                       aria-disabled="true"><i class="fas fa-fast-backward"></i></a>
                </li>

                <li class="page-item">
                    <a class="page-link" href="${url}?page=${page.getNumber()-1}&size=${page.getSize()}" tabindex="-1"
                       aria-disabled="true"><i class="fas fa-step-backward"></i></a>
                </li>

            <#else>
                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1" aria-disabled="true"><i class="fas fa-fast-backward"></i></a>
                </li>
                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1" aria-disabled="true"><i class="fas fa-step-backward"></i></a>
                </li>
            </#if>
            <#list body as p>
                <#if (p - 1)  == page.getNumber()>
                    <li class="page-item active">
                        <a class="page-link" href="${url}?page=${p}" tabindex="-1" aria-disabled="true">${p}</a>
                    </li>
                <#else>
                    <li class="page-item"><a class="page-link" href="${url}?page=${p-1}&size=${page.getSize()}">${p}</a>
                    </li>
                </#if>
            </#list>
            <#if pageNumber < totalPages>

                <li class="page-item">
                    <a class="page-link active" href="${url}?page=${page.getNumber()+1}&size=${page.getSize()}"><i class="fas fa-step-forward"></i></a>
                </li>

                <li class="page-item">
                    <a class="page-link active" href="${url}?page=${totalPages-1}&size=${page.getSize()}"><i class="fas fa-fast-forward"></i></a>
                </li>

            <#else>
                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1" aria-disabled="true"><i class="fas fa-step-forward"></i></a>
                </li>

                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1" aria-disabled="true"><i class="fas fa-fast-forward"></i></a>
                </li>

            </#if>
        </ul>

        <ul class="pagination justify-content-center">
            <li class="page-item">
                <a class="page-link" tabindex="-1" aria-disabled="true"><i class="fas fa-line-columns"></i></a>
            </li>
            <#list [5, 10, 15, 30, 50] as c>
                <#if c  == page.getSize()>
                    <li class="page-item active">
                        <a class="page-link" href="#" tabindex="-1" aria-disabled="true">${c}</a>
                    </li>
                <#else>
                    <li class="page-item"><a class="page-link" href="${url}?page=${page.getNumber()}&size=${c}">${c}</a>
                    </li>
                </#if>
            </#list>
        </ul>
    </div>
</#macro>