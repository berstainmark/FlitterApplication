package org.flitter.app.currency.etc.dao;

import org.flitter.app.currency.etc.model.SellEtc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellEtcRepository extends CrudRepository<SellEtc, Long> {

    @Query("select a from SellEtc a where a.xuuid=:xuuid")
    Optional<SellEtc> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellEtc a where a.iscloseexternal=:iscloseexternal")
    List<SellEtc> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
