<#list docs as doc>
    <section class="section">
        <div class="container-fluid">
            <div class="row-fluid title" id="section-${doc_index}">
                <h2>${doc.title}</h2>
            </div>
            <div class="row-fluid content">
                <#include "../../docs/${doc.doc}.html">
                <a href="${doc.wiki}" target="_blank" class="btn btn-primary pull-right">
                    了解更多
                    <i class="icon-circle-arrow-right icon-white"></i>
                </a>
            </div>
        </div>
    </section>
</#list>	