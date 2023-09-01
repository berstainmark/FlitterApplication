package org.flitter.app.currency.ltc.dao;

import org.flitter.app.currency.ltc.model.BuyLtc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyLtcRepository extends CrudRepository<BuyLtc, Long> {

    @Query("select a from BuyLtc a where a.xuuid=:xuuid")
    Optional<BuyLtc> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyLtc a where a.iscloseexternal=:iscloseexternal")
    List<BuyLtc> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyLtc a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyLtc> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyLtc a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyLtc> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);


}
