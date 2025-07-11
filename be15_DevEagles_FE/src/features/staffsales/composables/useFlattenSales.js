export function useFlattenSales({ categoryLabelMap, staffNameFilter }) {
  const flattenStaffSalesList = staffSalesList => {
    if (!staffSalesList) return [];
    const result = [];

    staffSalesList.forEach(staff => {
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
          totalSales: 0,
          totalDeductions: 0,
          finalSales: 0,
        };

        payment.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
          if (
            paymentsMethod !== 'PREPAID' &&
            Object.prototype.hasOwnProperty.call(row, paymentsMethod)
          ) {
            row[paymentsMethod] += amount;
            row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
            row.totalSales += amount;
          }
        });

        payment.deductionList?.forEach(({ deduction, amount }) => {
          if (Object.prototype.hasOwnProperty.call(row, deduction)) {
            row[deduction] += amount;
            row.totalDeductions += amount;
          }
        });

        row.finalSales = row.totalSales - row.totalDeductions;
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
      DISCOUNT: 0,
      COUPON: 0,
      PREPAID: 0,
      totalSales: 0,
      totalDeductions: 0,
      finalSales: 0,
    };

    result.forEach(row => {
      ['CARD', 'CASH', 'NAVER_PAY', 'LOCAL'].forEach(method => {
        totals[method] += row[method];
        totals[`${method}_INCENTIVE`] += row[`${method}_INCENTIVE`];
      });
      ['DISCOUNT', 'COUPON', 'PREPAID', 'totalSales', 'totalDeductions', 'finalSales'].forEach(
        key => {
          totals[key] += row[key] || 0;
        }
      );
    });

    result.push(totals);
    return result;
  };

  const flattenDetailData = staffSalesList => {
    if (!staffSalesList) return [];
    const result = [];

    staffSalesList.forEach(staff => {
      if (staffNameFilter.value && !staff.staffName.includes(staffNameFilter.value.trim())) return;

      const staffRows = [];
      let isFirstRow = true;

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
              totalSales: 0,
              totalDeductions: 0,
              finalSales: 0,
            };

            secondary.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
              if (
                paymentsMethod !== 'PREPAID' &&
                Object.prototype.hasOwnProperty.call(row, paymentsMethod)
              ) {
                row[paymentsMethod] += amount;
                row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
                row.totalSales += amount;
              }
            });

            secondary.deductionList?.forEach(({ deduction, amount }) => {
              if (Object.prototype.hasOwnProperty.call(row, deduction)) {
                row[deduction] += amount;
                row.totalDeductions += amount;
              }
            });

            row.finalSales = row.totalSales - row.totalDeductions;
            staffRows.push(row);
            isFirstRow = false;
          });
        });
      });

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
          totalSales: 0,
          totalDeductions: 0,
          finalSales: 0,
        };

        payment.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
          if (
            paymentsMethod !== 'PREPAID' &&
            Object.prototype.hasOwnProperty.call(row, paymentsMethod)
          ) {
            row[paymentsMethod] += amount;
            row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
            row.totalSales += amount;
          }
        });

        payment.deductionList?.forEach(({ deduction, amount }) => {
          if (Object.prototype.hasOwnProperty.call(row, deduction)) {
            row[deduction] += amount;
            row.totalDeductions += amount;
          }
        });

        row.finalSales = row.totalSales - row.totalDeductions;
        staffRows.push(row);
        isFirstRow = false;
      });

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
          key => {
            summaryRow[key] += row[key] || 0;
          }
        );
      });

      result.push(...staffRows, summaryRow);
    });

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
