package org.flitter.app.currency.eth.dao;

import org.flitter.app.currency.eth.model.SellEth;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellEthRepository extends CrudRepository<SellEth, Long> {

    @Query("select a from SellEth a where a.xuuid=:xuuid")
    Optional<SellEth> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellEth a where a.iscloseexternal=:iscloseexternal")
    List<SellEth> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
