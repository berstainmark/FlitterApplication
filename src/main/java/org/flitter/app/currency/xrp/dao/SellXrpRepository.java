package org.flitter.app.currency.xrp.dao;

import org.flitter.app.currency.xrp.model.SellXrp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellXrpRepository extends CrudRepository<SellXrp, Long> {

    @Query("select a from SellXrp a where a.xuuid=:xuuid")
    Optional<SellXrp> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellXrp a where a.iscloseexternal=:iscloseexternal")
    List<SellXrp> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
