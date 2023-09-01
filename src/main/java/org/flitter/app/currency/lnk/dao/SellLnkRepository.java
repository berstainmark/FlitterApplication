package org.flitter.app.currency.lnk.dao;

import org.flitter.app.currency.lnk.model.SellLnk;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellLnkRepository extends CrudRepository<SellLnk, Long> {

    @Query("select a from SellLnk a where a.xuuid=:xuuid")
    Optional<SellLnk> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellLnk a where a.iscloseexternal=:iscloseexternal")
    List<SellLnk> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
