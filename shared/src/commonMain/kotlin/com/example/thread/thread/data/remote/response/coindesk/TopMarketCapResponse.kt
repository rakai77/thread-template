package com.example.thread.thread.data.remote.response.coindesk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Top Market Cap REST Response
@Serializable
data class TopMarketCapResponse(
    @SerialName("Message") val message: String? = null,
    @SerialName("Type") val type: Int? = null,
    @SerialName("MetaData") val metaData: MarketCapMetaData? = null,
    @SerialName("SponsoredData") val sponsoredData: List<SponsoredCoin>? = null,
    @SerialName("Data") val data: List<CoinMarketCapData>? = null,
    @SerialName("RateLimit") val rateLimit: RateLimit? = null,
    @SerialName("HasWarning") val hasWarning: Boolean? = null
)

@Serializable
data class MarketCapMetaData(
    @SerialName("Count") val count: Int? = null
)

@Serializable
data class SponsoredCoin(
    @SerialName("CoinInfo") val coinInfo: CoinInfo? = null
)

@Serializable
data class CoinMarketCapData(
    @SerialName("CoinInfo") val coinInfo: CoinInfo,
    @SerialName("RAW") val raw: Map<String, RawMarketData>? = null,
    @SerialName("DISPLAY") val display: Map<String, DisplayMarketData>? = null
)

@Serializable
data class CoinInfo(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String,
    @SerialName("FullName") val fullName: String,
    @SerialName("Internal") val internal: String? = null,
    @SerialName("ImageUrl") val imageUrl: String? = null,
    @SerialName("Url") val url: String? = null,
    @SerialName("Algorithm") val algorithm: String? = null,
    @SerialName("ProofType") val proofType: String? = null,
    @SerialName("Rating") val rating: RatingInfo? = null,
    @SerialName("NetHashesPerSecond") val netHashesPerSecond: Double? = null,
    @SerialName("BlockNumber") val blockNumber: Long? = null,
    @SerialName("BlockTime") val blockTime: Double? = null,
    @SerialName("BlockReward") val blockReward: Double? = null,
    @SerialName("AssetLaunchDate") val assetLaunchDate: String? = null,
    @SerialName("MaxSupply") val maxSupply: Double? = null,
    @SerialName("Type") val type: Int? = null,
    @SerialName("DocumentType") val documentType: String? = null
)

@Serializable
data class RatingInfo(
    @SerialName("Weiss") val weiss: WeissRating? = null
)

@Serializable
data class WeissRating(
    @SerialName("Rating") val rating: String? = null,
    @SerialName("TechnologyAdoptionRating") val technologyAdoptionRating: String? = null,
    @SerialName("MarketPerformanceRating") val marketPerformanceRating: String? = null
)

@Serializable
data class RawMarketData(
    @SerialName("TYPE") val type: String? = null,
    @SerialName("MARKET") val market: String? = null,
    @SerialName("FROMSYMBOL") val fromSymbol: String? = null,
    @SerialName("TOSYMBOL") val toSymbol: String? = null,
    @SerialName("FLAGS") val flags: String? = null,
    @SerialName("PRICE") val price: Double? = null,
    @SerialName("LASTUPDATE") val lastUpdate: Long? = null,
    @SerialName("MEDIAN") val median: Double? = null,
    @SerialName("LASTVOLUME") val lastVolume: Double? = null,
    @SerialName("LASTVOLUMETO") val lastVolumeTo: Double? = null,
    @SerialName("LASTTRADEID") val lastTradeId: String? = null,
    @SerialName("VOLUMEDAY") val volumeDay: Double? = null,
    @SerialName("VOLUMEDAYTO") val volumeDayTo: Double? = null,
    @SerialName("VOLUME24HOUR") val volume24Hour: Double? = null,
    @SerialName("VOLUME24HOURTO") val volume24HourTo: Double? = null,
    @SerialName("OPENDAY") val openDay: Double? = null,
    @SerialName("HIGHDAY") val highDay: Double? = null,
    @SerialName("LOWDAY") val lowDay: Double? = null,
    @SerialName("OPEN24HOUR") val open24Hour: Double? = null,
    @SerialName("HIGH24HOUR") val high24Hour: Double? = null,
    @SerialName("LOW24HOUR") val low24Hour: Double? = null,
    @SerialName("LASTMARKET") val lastMarket: String? = null,
    @SerialName("VOLUMEHOUR") val volumeHour: Double? = null,
    @SerialName("VOLUMEHOURTO") val volumeHourTo: Double? = null,
    @SerialName("OPENHOUR") val openHour: Double? = null,
    @SerialName("HIGHHOUR") val highHour: Double? = null,
    @SerialName("LOWHOUR") val lowHour: Double? = null,
    @SerialName("TOPTIERVOLUME24HOUR") val topTierVolume24Hour: Double? = null,
    @SerialName("TOPTIERVOLUME24HOURTO") val topTierVolume24HourTo: Double? = null,
    @SerialName("CHANGE24HOUR") val change24Hour: Double? = null,
    @SerialName("CHANGEPCT24HOUR") val changePct24Hour: Double? = null,
    @SerialName("CHANGEDAY") val changeDay: Double? = null,
    @SerialName("CHANGEPCTDAY") val changePctDay: Double? = null,
    @SerialName("CHANGEHOUR") val changeHour: Double? = null,
    @SerialName("CHANGEPCTHOUR") val changePctHour: Double? = null,
    @SerialName("CONVERSIONTYPE") val conversionType: String? = null,
    @SerialName("CONVERSIONSYMBOL") val conversionSymbol: String? = null,
    @SerialName("SUPPLY") val supply: Double? = null,
    @SerialName("MKTCAP") val mktCap: Double? = null,
    @SerialName("MKTCAPPENALTY") val mktCapPenalty: Int? = null,
    @SerialName("CIRCULATINGSUPPLY") val circulatingSupply: Double? = null,
    @SerialName("CIRCULATINGSUPPLYMKTCAP") val circulatingSupplyMktCap: Double? = null,
    @SerialName("TOTALVOLUME24H") val totalVolume24H: Double? = null,
    @SerialName("TOTALVOLUME24HTO") val totalVolume24HTo: Double? = null,
    @SerialName("TOTALTOPTIERVOLUME24H") val totalTopTierVolume24H: Double? = null,
    @SerialName("TOTALTOPTIERVOLUME24HTO") val totalTopTierVolume24HTo: Double? = null,
    @SerialName("IMAGEURL") val imageUrl: String? = null
)

@Serializable
data class DisplayMarketData(
    @SerialName("FROMSYMBOL") val fromSymbol: String? = null,
    @SerialName("TOSYMBOL") val toSymbol: String? = null,
    @SerialName("MARKET") val market: String? = null,
    @SerialName("PRICE") val price: String? = null,
    @SerialName("LASTUPDATE") val lastUpdate: String? = null,
    @SerialName("LASTVOLUME") val lastVolume: String? = null,
    @SerialName("LASTVOLUMETO") val lastVolumeTo: String? = null,
    @SerialName("VOLUMEDAY") val volumeDay: String? = null,
    @SerialName("VOLUMEDAYTO") val volumeDayTo: String? = null,
    @SerialName("VOLUME24HOUR") val volume24Hour: String? = null,
    @SerialName("VOLUME24HOURTO") val volume24HourTo: String? = null,
    @SerialName("OPENDAY") val openDay: String? = null,
    @SerialName("HIGHDAY") val highDay: String? = null,
    @SerialName("LOWDAY") val lowDay: String? = null,
    @SerialName("OPEN24HOUR") val open24Hour: String? = null,
    @SerialName("HIGH24HOUR") val high24Hour: String? = null,
    @SerialName("LOW24HOUR") val low24Hour: String? = null,
    @SerialName("LASTMARKET") val lastMarket: String? = null,
    @SerialName("CHANGE24HOUR") val change24Hour: String? = null,
    @SerialName("CHANGEPCT24HOUR") val changePct24Hour: String? = null,
    @SerialName("CHANGEDAY") val changeDay: String? = null,
    @SerialName("CHANGEPCTDAY") val changePctDay: String? = null,
    @SerialName("SUPPLY") val supply: String? = null,
    @SerialName("MKTCAP") val mktCap: String? = null,
    @SerialName("TOTALVOLUME24H") val totalVolume24H: String? = null,
    @SerialName("TOTALVOLUME24HTO") val totalVolume24HTo: String? = null,
    @SerialName("TOTALTOPTIERVOLUME24H") val totalTopTierVolume24H: String? = null,
    @SerialName("TOTALTOPTIERVOLUME24HTO") val totalTopTierVolume24HTo: String? = null,
    @SerialName("IMAGEURL") val imageUrl: String? = null
)

@Serializable
data class RateLimit(
    @SerialName("calls_made") val callsMade: CallsInfo? = null,
    @SerialName("calls_left") val callsLeft: CallsInfo? = null
)

@Serializable
data class CallsInfo(
    @SerialName("second") val second: Int? = null,
    @SerialName("minute") val minute: Int? = null,
    @SerialName("hour") val hour: Int? = null,
    @SerialName("day") val day: Int? = null,
    @SerialName("month") val month: Int? = null
)

// WebSocket Top Market Cap Update
@Serializable
data class TopMarketCapWSMessage(
    @SerialName("TYPE") val type: String,
    @SerialName("MESSAGE") val message: String? = null,
    @SerialName("INFO") val info: String? = null,
    @SerialName("PARAMETER") val parameter: String? = null,
    @SerialName("STREAMING_DATA") val streamingData: List<CoinMarketCapData>? = null
)
