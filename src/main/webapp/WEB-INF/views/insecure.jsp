<%@ page contentType="text/html;charset=UTF-8" %>
<% String contextPath = request.getContextPath(); %>
<section class="py-5 min-vh-100 d-flex justify-content-center align-items-center">
    <div class="container">
        <div class="row">
            <div class="col-12 text-center">
                <h2 class="mb-4 d-flex justify-content-center align-items-center gap-3">
                    <i class="bi bi-shield-exclamation text-warning display-3"></i>
                    <span class="display-4 fw-bold">Insecure Connection</span>
                </h2>
                <h3 class="h5 mb-3">Your connection to this site is not secure.</h3>
                <p class="mb-4">
                    Attackers might be trying to steal your information from this site (for example, passwords, messages, or credit cards).
                    Please proceed with caution.
                </p>

                <button class="btn btn-danger rounded-pill px-4 py-2 mb-3" onclick="window.history.back()">Go Back to Safety</button>
            </div>
        </div>
    </div>
</section>