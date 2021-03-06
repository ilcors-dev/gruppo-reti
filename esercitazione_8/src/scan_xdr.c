/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "scan.h"

bool_t
xdr_Output (XDR *xdrs, Output *objp)
{
	register int32_t *buf;


	if (xdrs->x_op == XDR_ENCODE) {
		buf = XDR_INLINE (xdrs, 4 * BYTES_PER_XDR_UNIT);
		if (buf == NULL) {
			 if (!xdr_int (xdrs, &objp->caratteri))
				 return FALSE;
			 if (!xdr_int (xdrs, &objp->parole))
				 return FALSE;
			 if (!xdr_int (xdrs, &objp->linee))
				 return FALSE;
			 if (!xdr_int (xdrs, &objp->errore))
				 return FALSE;
		} else {
			IXDR_PUT_LONG(buf, objp->caratteri);
			IXDR_PUT_LONG(buf, objp->parole);
			IXDR_PUT_LONG(buf, objp->linee);
			IXDR_PUT_LONG(buf, objp->errore);
		}
		return TRUE;
	} else if (xdrs->x_op == XDR_DECODE) {
		buf = XDR_INLINE (xdrs, 4 * BYTES_PER_XDR_UNIT);
		if (buf == NULL) {
			 if (!xdr_int (xdrs, &objp->caratteri))
				 return FALSE;
			 if (!xdr_int (xdrs, &objp->parole))
				 return FALSE;
			 if (!xdr_int (xdrs, &objp->linee))
				 return FALSE;
			 if (!xdr_int (xdrs, &objp->errore))
				 return FALSE;
		} else {
			objp->caratteri = IXDR_GET_LONG(buf);
			objp->parole = IXDR_GET_LONG(buf);
			objp->linee = IXDR_GET_LONG(buf);
			objp->errore = IXDR_GET_LONG(buf);
		}
	 return TRUE;
	}

	 if (!xdr_int (xdrs, &objp->caratteri))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->parole))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->linee))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->errore))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Input (XDR *xdrs, Input *objp)
{
	register int32_t *buf;

	 if (!xdr_string (xdrs, &objp->directory, 50))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->soglia))
		 return FALSE;
	return TRUE;
}
