package org.flitter.app.currency.ada.dao;

import org.flitter.app.currency.ada.model.SellAda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellAdaRepository extends CrudRepository<SellAda, Long> {

    @Query("select a from SellAda a where a.xuuid=:xuuid")
    Optional<SellAda> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellAda a where a.iscloseexternal=:iscloseexternal")
    List<SellAda> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
