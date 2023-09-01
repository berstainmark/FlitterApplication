package org.flitter.app.currency.sol.dao;

import org.flitter.app.currency.sol.model.SellSol;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellSolRepository extends CrudRepository<SellSol, Long> {

    @Query("select a from SellSol a where a.xuuid=:xuuid")
    Optional<SellSol> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellSol a where a.iscloseexternal=:iscloseexternal")
    List<SellSol> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
