package be.dnsbelgium.rdap.controller;

import be.dnsbelgium.rdap.core.DomainsSearchResult;
import be.dnsbelgium.rdap.core.Nameserver;
import be.dnsbelgium.rdap.core.RDAPError;
import be.dnsbelgium.rdap.service.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "domains")
public class SearchDomainsController {

	private final static Logger logger = LoggerFactory.getLogger(SearchDomainsController.class);

	private final DomainService domainService;

	@Autowired
	public SearchDomainsController(DomainService domainService) {
		this.domainService = domainService;
	}

	@RequestMapping(method = RequestMethod.GET, produces = Controllers.CONTENT_TYPE)
	@ResponseBody
	public DomainsSearchResult search(@RequestParam(value = "name", required = false) final String name,
			@RequestParam(value = "nsLdhName", required = false) final String nsLdhName,
			@RequestParam(value = "nsIp", required = false) final String nsIp) throws RDAPError {
		checkParams(name, nsLdhName, nsIp);
		if (name != null) {
			return handleByNameSearch(name);
		}
		if (nsLdhName != null) {
			return handleByNsLdhNameSearch(nsLdhName);
		}
		return handleByNsIpSearch(nsIp);
	}

	@RequestMapping(method = { RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.PATCH,
			RequestMethod.POST, RequestMethod.TRACE }, produces = Controllers.CONTENT_TYPE)
	@ResponseBody
	public Nameserver any(@RequestParam(value = "name", required = false) final String name,
			@RequestParam(value = "nsLdhName", required = false) final String nsLdhName,
			@RequestParam(value = "nsIp", required = false) final String nsIp) throws RDAPError {
		throw RDAPError.methodNotAllowed();
	}

	private DomainsSearchResult handleByNsIpSearch(String nsIp) throws RDAPError {
		DomainsSearchResult domains = domainService.searchDomainsByNsIp(nsIp);
		if (domains == null) {
			throw RDAPError.noResults(nsIp);
		}
		return domains;
	}

	private DomainsSearchResult handleByNsLdhNameSearch(String nsLdhName) throws RDAPError {
		DomainsSearchResult domains = domainService.searchDomainsByNsLdhName(nsLdhName);
		if (domains == null) {
			throw RDAPError.noResults(nsLdhName);
		}
		return domains;
	}

	private DomainsSearchResult handleByNameSearch(String name) throws RDAPError {
		DomainsSearchResult domains = domainService.searchDomainsByName(name);
		if (domains == null || domains.domainSearchResults == null || domains.domainSearchResults.isEmpty()) {
			throw RDAPError.noResults(name);
		}
		return domains;
	}

	private void checkParams(String name, String nsLdhName, String nsIp) throws RDAPError {
		if (name == null && nsLdhName == null && nsIp == null) {
			throw RDAPError.badRequest("Param missing",
					"One and only one of 'name', 'nsLdhName' or 'nsIp' should be provided");
		}
		int paramCount = 0;
		if (name != null) {
			paramCount++;
		}
		if (nsLdhName != null) {
			paramCount++;
		}
		if (nsIp != null) {
			paramCount++;
		}
		if (paramCount > 1) {
			throw RDAPError.badRequest("Too many params",
					"One and only one of 'name', 'nsLdhName' or 'nsIp' should be provided");
		}
	}
}
