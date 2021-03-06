/**
 * Copyright (c) 2013 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.emf.test.common.reification;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bounded Generic Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.test.common.reification.BoundedGenericContainer#getContent <em>Content</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.test.common.reification.ReificationPackage#getBoundedGenericContainer()
 * @model
 * @generated
 */
public interface BoundedGenericContainer<T extends Medium> extends EObject
{
  /**
   * Returns the value of the '<em><b>Content</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Content</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Content</em>' reference.
   * @see #setContent(Medium)
   * @see org.eclipse.emf.test.common.reification.ReificationPackage#getBoundedGenericContainer_Content()
   * @model
   * @generated
   */
  T getContent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.test.common.reification.BoundedGenericContainer#getContent <em>Content</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Content</em>' reference.
   * @see #getContent()
   * @generated
   */
  void setContent(T value);

} // BoundedGenericContainer
