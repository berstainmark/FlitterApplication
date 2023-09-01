package org.flitter.app.currency.btc.dao;

import org.flitter.app.currency.btc.model.SellBtc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellBtcRepository extends CrudRepository<SellBtc, Long> {

    @Query("select a from SellBtc a where a.xuuid=:xuuid")
    Optional<SellBtc> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellBtc a where a.iscloseexternal=:iscloseexternal")
    List<SellBtc> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
