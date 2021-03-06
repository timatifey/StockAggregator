package dev.timatifey.stockaggregator.fragments.main.list.stocks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager

import dev.timatifey.stockaggregator.R
import dev.timatifey.stockaggregator.data.model.Stock
import dev.timatifey.stockaggregator.utils.PriceChanges
import dev.timatifey.stockaggregator.utils.withCurrency
import dev.timatifey.stockaggregator.viewmodel.stocks.StocksViewModel

open class StocksAdapter(
    private val glide: RequestManager,
    private val stocksViewModel: StocksViewModel,
) : RecyclerView.Adapter<StocksAdapter.StockViewHolder>() {

    var stockList = emptyList<Stock>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: AppCompatTextView = itemView.findViewById(R.id.list_item__stock__name)
        val tvTicket: AppCompatTextView = itemView.findViewById(R.id.list_item__stock__ticket)
        val tvCurrentPrice: AppCompatTextView =
            itemView.findViewById(R.id.list_item__stock__current_price)
        val ivLogo: AppCompatImageView = itemView.findViewById(R.id.list_item__stock__logo)
        val tvPriceChanges: AppCompatTextView =
            itemView.findViewById(R.id.list_item__stock__price_changes)
        val btnLike: AppCompatButton = itemView.findViewById(R.id.list_item__stock__like_btn)

        val colorPriceRaises = ContextCompat.getColor(itemView.context, R.color.price_raises)
        val colorPriceFalls = ContextCompat.getColor(itemView.context, R.color.price_falls)

        val colorStateShadow =
            ContextCompat.getColorStateList(itemView.context, R.color.star_shadow)
        val colorStateYellow =
            ContextCompat.getColorStateList(itemView.context, R.color.star_yellow)

        val colorBackgroundGray =
            ContextCompat.getDrawable(itemView.context, R.drawable.custom__background_gray)
        val colorBackgroundWhite =
            ContextCompat.getDrawable(itemView.context, R.drawable.custom__background_white)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item__stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val currentItem = stockList[position]
        val changes = PriceChanges(
            currentItem.currentPrice,
            currentItem.previousClosePrice,
            currentItem.currency
        )

        holder.apply {
            tvName.text = currentItem.name
            tvTicket.text = currentItem.ticker
            tvCurrentPrice.text = currentItem.currentPrice.withCurrency(currentItem.currency)
            glide.load(currentItem.logo).into(ivLogo)
            itemView.background = if (position % 2 == 0)
                colorBackgroundGray
            else
                colorBackgroundWhite

            tvPriceChanges.text = changes.viewString
            tvPriceChanges.setTextColor(
                if (changes.priceIsRaising) colorPriceRaises else colorPriceFalls
            )

            btnLike.backgroundTintList =
                if (currentItem.isFavourite) colorStateYellow else colorStateShadow

            btnLike.setOnClickListener { button ->
                if (currentItem.isFavourite) {
                    currentItem.isFavourite = false
                    button.backgroundTintList = colorStateShadow
                } else {
                    currentItem.isFavourite = true
                    button.backgroundTintList = colorStateYellow
                }
                afterLikeClick(currentItem)
            }

            itemView.setOnClickListener {
                Toast.makeText(
                    itemView.context, "Clicked on ${currentItem.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    protected open fun afterLikeClick(currentItem: Stock) {
        stocksViewModel.updateStock(currentItem)
    }

    override fun getItemCount(): Int {
        return stockList.size
    }
}
