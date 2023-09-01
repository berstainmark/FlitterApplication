package org.flitter.app.currency.ada.dao;

import org.flitter.app.currency.ada.model.BuyAda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyAdaRepository extends CrudRepository<BuyAda, Long> {

    @Query("select a from BuyAda a where a.xuuid=:xuuid")
    Optional<BuyAda> findBuyId(@Param("xuuid")String xuuid);

    @Query("select a from BuyAda a where a.iscloseexternal=:iscloseexternal")
    List<BuyAda> findByCloseExternal(@Param("iscloseexternal")Integer iscloseexternal);

    @Query("select a from BuyAda a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyAda> findBuyMaxPrice(@Param("isclose")Integer isclose, @Param("maxprice")Double maxprice);

    @Query("select a from BuyAda a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyAda> findMarginPrice(@Param("isclose")Integer isclose,@Param("price")Double price,  @Param("margin")Double margin);

}
