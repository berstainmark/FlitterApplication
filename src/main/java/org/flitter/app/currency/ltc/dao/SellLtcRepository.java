package org.flitter.app.currency.ltc.dao;

import org.flitter.app.currency.ltc.model.SellLtc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellLtcRepository extends CrudRepository<SellLtc, Long> {

    @Query("select a from SellLtc a where a.xuuid=:xuuid")
    Optional<SellLtc> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellLtc a where a.iscloseexternal=:iscloseexternal")
    List<SellLtc> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
