package br.com.cdb.bancodigital.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import br.com.cdb.bancodigital.service.CartaoService;
import br.com.cdb.bancodigital.service.ContaService;

@Configuration
public class ScheduledTasks {

    @Autowired
    private ContaService contaService;

    @Autowired
    private CartaoService cartaoService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void aplicarTaxaMensal() {
        contaService.aplicarTaxaMensalTodasContas();
    }

    @Scheduled(cron = "0 0 0 L * ?")
    public void aplicarTaxaCartoes() {
        cartaoService.aplicarTaxaTodasCartoes();
    }
}