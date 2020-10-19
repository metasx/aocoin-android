package com.aocoin.wallet.pages.wallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.pages.wallet.type.WalletMnemonicItem
import com.aocoin.wallet.R
import kotlinx.android.synthetic.main.item_wallet_mnemonic.view.*

/**
 * @FileName WalletMnemonicAdapter
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 1:28 PM
 */
class WalletMnemonicAdapter(
    private val mMnemonics: List<WalletMnemonicItem>,
    private var mSelectedMnemonics: ArrayList<WalletMnemonicItem> = arrayListOf(),
    private val onItemClickListener: (WalletMnemonicItem) -> Unit = {})
    : RecyclerView.Adapter<WalletMnemonicAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet_mnemonic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val item = mMnemonics[position]

            val context = word_tv?.context!!

            word_tv?.text = item.word

            word_tv?.setTextColor(
                    ContextCompat.getColor(context,
                            if (mSelectedMnemonics.contains(item))
                                R.color.color_red
                            else
                                R.color.qmui_config_color_white))

            word_tv?.setOnClickListener {
                onItemClickListener.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int = mMnemonics.size
}