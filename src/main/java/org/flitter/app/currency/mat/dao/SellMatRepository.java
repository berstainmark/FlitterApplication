package org.flitter.app.currency.mat.dao;

import org.flitter.app.currency.mat.model.SellMat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellMatRepository extends CrudRepository<SellMat, Long> {

    @Query("select a from SellMat a where a.xuuid=:xuuid")
    Optional<SellMat> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellMat a where a.iscloseexternal=:iscloseexternal")
    List<SellMat> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
