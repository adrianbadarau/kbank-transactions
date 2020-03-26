package com.adrianbadarau.bank.transactions.client

import com.adrianbadarau.bank.products.domain.ClientAccount
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(serviceId = "products")
interface ClientAccountsFeignClient {

    @RequestMapping(value = ["/api/client-accounts"], method = [RequestMethod.GET])
    fun getAllClientAccounts(): List<ClientAccount>

    @RequestMapping(value = ["/api/client-accounts"], method = [RequestMethod.PUT])
    fun updateAccountBalance(clientAccount: ClientAccount): ClientAccount

    @RequestMapping(value = ["/api/client-accounts/{id}"], method = [RequestMethod.GET])
    fun getCustomerAccount(@PathVariable id: String): ClientAccount
}
