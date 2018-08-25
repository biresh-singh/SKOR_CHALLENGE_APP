package event;

import bean.NewEVoucher;

/**
 * Created by jessica on 12/28/17.
 */

public class VoucherEvent {

    public static class VoucherClicked{
        private NewEVoucher newEVoucher;

        public VoucherClicked(NewEVoucher newEVoucher) {
            this.newEVoucher = newEVoucher;
        }

        public NewEVoucher getNewEVoucher() {
            return newEVoucher;
        }
    }

    public static class VoucherRedeemed{
        private int voucherId;

        public VoucherRedeemed(int voucherId) {
            this.voucherId = voucherId;
        }

        public int getVoucherId() {
            return voucherId;
        }
    }
}
