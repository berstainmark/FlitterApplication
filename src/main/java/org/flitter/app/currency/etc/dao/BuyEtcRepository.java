package org.flitter.app.currency.etc.dao;

import org.flitter.app.currency.etc.model.BuyEtc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyEtcRepository extends CrudRepository<BuyEtc, Long> {

    @Query("select a from BuyEtc a where a.xuuid=:xuuid")
    Optional<BuyEtc> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyEtc a where a.iscloseexternal=:iscloseexternal")
    List<BuyEtc> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyEtc a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyEtc> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyEtc a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyEtc> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);

}
