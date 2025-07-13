export function useFlattenSales({ categoryLabelMap, staffNameFilter }) {
  const flattenStaffSalesList = data => {
    if (!data.staffSalesList) return [];
    const result = [];

    data.staffSalesList.forEach(staff => {
      if (staffNameFilter.value && !staff.staffName.includes(staffNameFilter.value.trim())) return;

      let isFirstRow = true;
      staff.paymentsSalesList.forEach(payment => {
        const row = {
          name: isFirstRow ? staff.staffName : '',
          category: categoryLabelMap[payment.category] || payment.category,
          CARD: 0,
          CASH: 0,
          NAVER_PAY: 0,
          LOCAL: 0,
          CARD_INCENTIVE: 0,
          CASH_INCENTIVE: 0,
          NAVER_PAY_INCENTIVE: 0,
          LOCAL_INCENTIVE: 0,
          DISCOUNT: 0,
          COUPON: 0,
          PREPAID: 0,
          totalSales: payment.netSalesTotal,
          totalDeductions: payment.deductionTotal,
          finalSales: payment.grossSalesTotal,
        };

        payment.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
          if (
            paymentsMethod !== 'PREPAID' &&
            Object.prototype.hasOwnProperty.call(row, paymentsMethod)
          ) {
            row[paymentsMethod] += amount;
            row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
          }
        });

        payment.deductionList?.forEach(({ deduction, amount }) => {
          if (Object.prototype.hasOwnProperty.call(row, deduction)) {
            row[deduction] += amount;
          }
        });

        result.push(row);
        isFirstRow = false;
      });
    });

    const totals = {
      name: '총계',
      category: '',
      CARD: 0,
      CASH: 0,
      NAVER_PAY: 0,
      LOCAL: 0,
      CARD_INCENTIVE: 0,
      CASH_INCENTIVE: 0,
      NAVER_PAY_INCENTIVE: 0,
      LOCAL_INCENTIVE: 0,
      DISCOUNT: data.totalSummary.totalDiscount,
      COUPON: data.totalSummary.totalCoupon,
      PREPAID: data.totalSummary.totalPrepaid,
      totalSales: data.totalSummary.totalNetSales,
      totalDeductions: data.totalSummary.totalDeduction,
      finalSales: data.totalSummary.totalGrossSales,
    };

    result.forEach(row => {
      ['CARD', 'CASH', 'NAVER_PAY', 'LOCAL'].forEach(method => {
        totals[method] += row[method];
        totals[`${method}_INCENTIVE`] += row[`${method}_INCENTIVE`];
      });
    });
    result.push(totals);
    return result;
  };

  const flattenDetailData = data => {
    if (!data.staffSalesList) return [];
    const result = [];

    const totals = {
      name: '총계',
      category: '',
      primary: '',
      secondary: '',
      CARD: 0,
      CASH: 0,
      NAVER_PAY: 0,
      LOCAL: 0,
      CARD_INCENTIVE: 0,
      CASH_INCENTIVE: 0,
      NAVER_PAY_INCENTIVE: 0,
      LOCAL_INCENTIVE: 0,
      DISCOUNT: data.totalSummary.totalDiscount,
      COUPON: data.totalSummary.totalCoupon,
      PREPAID: data.totalSummary.totalPrepaid,
      totalSales: data.totalSummary.totalNetSales,
      totalDeductions: data.totalSummary.totalDeduction,
      finalSales: data.totalSummary.totalGrossSales,
    };

    data.staffSalesList.forEach(staff => {
      if (staffNameFilter.value && !staff.staffName.includes(staffNameFilter.value.trim())) return;

      const staffRows = [];
      let isFirstRow = true;

      // 상세 매출 (SERVICE / PRODUCT)
      staff.paymentsDetailSalesList?.forEach(payment => {
        const categoryLabel = categoryLabelMap[payment.category] || payment.category;
        payment.primaryList?.forEach(primary => {
          const primaryName = primary.primaryItemName;
          primary.secondaryList?.forEach(secondary => {
            const row = {
              name: isFirstRow ? staff.staffName : '',
              category: categoryLabel,
              primary: primaryName,
              secondary: secondary.secondaryItemName,
              CARD: 0,
              CASH: 0,
              NAVER_PAY: 0,
              LOCAL: 0,
              CARD_INCENTIVE: 0,
              CASH_INCENTIVE: 0,
              NAVER_PAY_INCENTIVE: 0,
              LOCAL_INCENTIVE: 0,
              DISCOUNT: 0,
              COUPON: 0,
              PREPAID: 0,
              totalSales: secondary.netSalesTotal,
              totalDeductions: secondary.deductionTotal,
              finalSales: secondary.grossSalesTotal,
            };

            secondary.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
              if (
                paymentsMethod !== 'PREPAID' &&
                Object.prototype.hasOwnProperty.call(row, paymentsMethod)
              ) {
                row[paymentsMethod] += amount;
                row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
              }
            });

            secondary.deductionList?.forEach(({ deduction, amount }) => {
              if (Object.prototype.hasOwnProperty.call(row, deduction)) {
                row[deduction] += amount;
              }
            });

            staffRows.push(row);
            isFirstRow = false;
          });
        });
      });

      // 일반 매출 (SESSION_PASS / PREPAID_PASS)
      staff.paymentsSalesList?.forEach(payment => {
        const row = {
          name: isFirstRow ? staff.staffName : '',
          category: categoryLabelMap[payment.category] || payment.category,
          primary: '',
          secondary: '',
          CARD: 0,
          CASH: 0,
          NAVER_PAY: 0,
          LOCAL: 0,
          CARD_INCENTIVE: 0,
          CASH_INCENTIVE: 0,
          NAVER_PAY_INCENTIVE: 0,
          LOCAL_INCENTIVE: 0,
          DISCOUNT: 0,
          COUPON: 0,
          PREPAID: 0,
          totalSales: payment.netSalesTotal,
          totalDeductions: payment.deductionTotal,
          finalSales: payment.grossSalesTotal,
        };

        payment.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
          if (
            paymentsMethod !== 'PREPAID' &&
            Object.prototype.hasOwnProperty.call(row, paymentsMethod)
          ) {
            row[paymentsMethod] += amount;
            row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
          }
        });

        payment.deductionList?.forEach(({ deduction, amount }) => {
          if (Object.prototype.hasOwnProperty.call(row, deduction)) {
            row[deduction] += amount;
          }
        });

        staffRows.push(row);
        isFirstRow = false;
      });

      // 직원별 총계
      const summaryRow = {
        name: '',
        category: '총계',
        primary: '',
        secondary: '',
        CARD: 0,
        CASH: 0,
        NAVER_PAY: 0,
        LOCAL: 0,
        CARD_INCENTIVE: 0,
        CASH_INCENTIVE: 0,
        NAVER_PAY_INCENTIVE: 0,
        LOCAL_INCENTIVE: 0,
        DISCOUNT: 0,
        COUPON: 0,
        PREPAID: 0,
        totalSales: 0,
        totalDeductions: 0,
        finalSales: 0,
      };

      staffRows.forEach(row => {
        ['CARD', 'CASH', 'NAVER_PAY', 'LOCAL'].forEach(method => {
          summaryRow[method] += row[method];
          summaryRow[`${method}_INCENTIVE`] += row[`${method}_INCENTIVE`];
        });
        ['DISCOUNT', 'COUPON', 'PREPAID', 'totalSales', 'totalDeductions', 'finalSales'].forEach(
          method => {
            summaryRow[method] += row[method];
          }
        );
      });
      result.push(...staffRows, summaryRow);
    });

    result.forEach(row => {
      if (row.category === '총계') return;
      ['CARD', 'CASH', 'NAVER_PAY', 'LOCAL'].forEach(method => {
        totals[method] += row[method];
        totals[`${method}_INCENTIVE`] += row[`${method}_INCENTIVE`];
      });
      ['DISCOUNT', 'COUPON', 'PREPAID', 'totalSales', 'totalDeductions', 'finalSales'].forEach(
        method => {
          totals[method] += row[method];
        }
      );
    });

    result.push(totals);
    return result;
  };

  const flattenTargetSales = staffSalesList => {
    if (!staffSalesList) return [];
    const result = [];
    staffSalesList.forEach(staff => {
      if (staffNameFilter.value && !staff.staffName.includes(staffNameFilter.value.trim())) return;
      let isFirst = true;
      staff.targetSalesList?.forEach(item => {
        result.push({
          name: isFirst ? staff.staffName : '',
          category: item.label,
          target: item.targetAmount,
          actual: item.totalAmount,
          rate: item.achievementRate,
        });
        isFirst = false;
      });
      result.push({
        name: '',
        category: '총계',
        target: staff.totalTargetAmount,
        actual: staff.totalActualAmount,
        rate: staff.totalAchievementRate,
      });
    });
    return result;
  };

  return {
    flattenStaffSalesList,
    flattenDetailData,
    flattenTargetSales,
  };
}
