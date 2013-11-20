<nav id="nav" class="navigation" role="navigation">
    <ul>
        <#list docs as doc>
        <li class="active">
            <a href="#section-${doc_index}">
                <i class="icon-${doc["icon"]} icon-white"></i> <span>${doc["title"]}</span>
            </a>
        </li>
        </#list>
    </ul>
</nav>